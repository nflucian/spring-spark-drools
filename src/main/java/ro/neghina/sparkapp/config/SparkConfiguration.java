package ro.neghina.sparkapp.config;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfiguration {

    @Value("${app.name:spring-drools}")
    private String appName;

    @Value("${spark.master:local[*]}")
    private String master;

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName(appName)
                .setMaster(master);
    }

    @Bean
    public JavaSparkContext ctx() {
        return new JavaSparkContext(sparkConf());
    }
}
