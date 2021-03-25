package edu.iu.uits.lms.reports.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "reports")
@Getter
@Setter
public class ToolConfig {

   private String version;
   private String env;
   private List<String> instructorRoles;
}
