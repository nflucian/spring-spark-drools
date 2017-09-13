package ro.neghina.sparkapp.services;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ro.neghina.sparkapp.entities.LangRating;
import ro.neghina.sparkapp.entities.WikipediaArticle;
import scala.Tuple2;

@Service
public class WikipediaService {

    @Value("${input.file}")
    private String inputFile;

    private final JavaSparkContext ctx;
    private final KieBase keiBase;

    @Autowired
    public WikipediaService(final JavaSparkContext ctx, final KieBase keiBase) {
        this.ctx = Objects.requireNonNull(ctx, "ctx should not be null");
        this.keiBase = Objects.requireNonNull(keiBase, "keiBase should not be null");
    }

    public List<LangRating> process(final List<String> langs) throws IOException {
        final Broadcast<KieBase> broadcastRules = ctx.broadcast(keiBase);

        Resource resource = new ClassPathResource(inputFile);

        final JavaRDD<WikipediaArticle> wikiRdd = ctx.textFile(resource.getURI().getPath())
                .map(line -> {
                    String subs = "</title><text>";
                    int i = line.indexOf(subs);
                    String title = line.substring(14, i);
                    String text = line.substring(i + subs.length(), line.length() - 16);
                    return new WikipediaArticle(title, text);
                });

        return wikiRdd.flatMapToPair(article ->
                langs.stream()
                        .filter(lang -> article.getText().contains(lang))
                        .map(lang -> new Tuple2<>(lang, 1)).iterator())
            .reduceByKey((a, b) -> a + b)
            .mapToPair(item -> item.swap())
            .sortByKey(false)
            .map(item ->
                LangRating.builder()
                        .score(item._1())
                        .lang(item._2())
                        .approved(false)
                        .build())
            .map(item -> applyRules(broadcastRules.value(), item))
            .filter(LangRating::getApproved)
            .collect();
    }

    public static LangRating applyRules(final KieBase base, final LangRating item) {
        StatelessKieSession session = base.newStatelessKieSession();
        session.execute(CommandFactory.newInsert(item));
        return item;
    }
}
