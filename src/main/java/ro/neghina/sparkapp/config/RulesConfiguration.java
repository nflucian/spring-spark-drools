package ro.neghina.sparkapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

@Configuration
public class RulesConfiguration {

    @Bean
    public KieBase kieBase() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();

        return kieContainer.getKieBase();
    }
}
