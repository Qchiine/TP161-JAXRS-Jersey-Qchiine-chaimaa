package ma.fstg.banque.config;

import ma.fstg.banque.web.CompteRestJaxRSAPI;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declaration du resource config Jersey.
 * <p>
 * Spring Boot enregistre automatiquement le servlet Jersey des qu'un bean
 * {@code ResourceConfig} est present : il suffit donc de lister la ressource.
 */
@Configuration
public class MyConfig {

    /**
     * @return la configuration Jersey contenant la ressource des comptes
     */
    @Bean
    public ResourceConfig resourceConfig() {
        ResourceConfig configuration = new ResourceConfig();
        configuration.register(CompteRestJaxRSAPI.class);
        return configuration;
    }
}
