package edu.iu.uits.lms.reports.config;


import edu.iu.uits.lms.common.swagger.LmsRepositoryDetectionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@Slf4j
public class JpaRestConfig implements RepositoryRestConfigurer {

    @Autowired
    private SpringDocConfigProperties springDocConfigProperties;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);
        config.setRepositoryDetectionStrategy(new LmsRepositoryDetectionStrategy(springDocConfigProperties.getPackagesToScan()));
    }
}