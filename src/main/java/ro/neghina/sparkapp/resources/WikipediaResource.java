package ro.neghina.sparkapp.resources;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.neghina.sparkapp.entities.LangRating;
import ro.neghina.sparkapp.services.WikipediaService;

@RestController
public class WikipediaResource {

    private final WikipediaService service;

    @Autowired
    public WikipediaResource(final WikipediaService service) {
        this.service = service;
    }

    @PostMapping()
    public List<LangRating> process(@RequestBody final List<String> langs) throws IOException {
        return service.process(langs);
    }
}
