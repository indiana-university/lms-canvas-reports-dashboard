package edu.iu.uits.lms.reports.services.swagger;

import edu.iu.uits.lms.lti.swagger.AbstractSwaggerDisabledTest;
import edu.iu.uits.lms.reports.WebApplication;
import edu.iu.uits.lms.reports.config.SecurityConfig;
import edu.iu.uits.lms.reports.services.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {WebApplication.class, SecurityConfig.class, TestConfig.class}, properties = {"lms.rabbitmq.queue_env_suffix = test"})
public class SwaggerDisabledTest extends AbstractSwaggerDisabledTest {

   @Override
   protected List<String> getEmbeddedSwaggerToolPaths() {
      return SwaggerTestUtil.getEmbeddedSwaggerToolPaths(super.getEmbeddedSwaggerToolPaths());
   }
}
