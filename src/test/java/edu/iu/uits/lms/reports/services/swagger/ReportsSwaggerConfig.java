package edu.iu.uits.lms.reports.services.swagger;

import edu.iu.uits.lms.iuonly.config.IuCustomRestConfiguration;
import edu.iu.uits.lms.lti.config.LtiRestConfiguration;
import edu.iu.uits.lms.lti.service.LmsDefaultGrantedAuthoritiesMapper;
import edu.iu.uits.lms.lti.swagger.SwaggerTestingBean;
import edu.iu.uits.lms.reports.config.SecurityConfig;
import edu.iu.uits.lms.reports.config.SwaggerConfig;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.IUCUSTOM_GROUP_CODE_PATH;

@Configuration
@Import({
        SecurityConfig.class,
        SwaggerConfig.class,
        edu.iu.uits.lms.lti.config.SwaggerConfig.class,
        LtiRestConfiguration.class,
        edu.iu.uits.lms.iuonly.config.SwaggerConfig.class,
        IuCustomRestConfiguration.class
})

public class ReportsSwaggerConfig {

    @MockBean
    private BufferingApplicationStartup bufferingApplicationStartup;

    @MockBean
    LmsDefaultGrantedAuthoritiesMapper lmsDefaultGrantedAuthoritiesMapper;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SwaggerTestingBean swaggerTestingBean() {
        SwaggerTestingBean stb = new SwaggerTestingBean();

        List<String> expandedList = new ArrayList<>();
        expandedList.add(IUCUSTOM_GROUP_CODE_PATH);

        stb.setEmbeddedSwaggerToolPaths(expandedList);
        return stb;
    }

}