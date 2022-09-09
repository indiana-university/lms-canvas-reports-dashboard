package edu.iu.uits.lms.reports.services;

import edu.iu.uits.lms.iuonly.AbstractIuCustomRestEnabledLaunchSecurityTest;
import edu.iu.uits.lms.reports.config.ToolConfig;
import org.springframework.context.annotation.Import;

@Import({ToolConfig.class, TestConfig.class})
public class IuCustomRestEnabledLaunchSecurityTest extends AbstractIuCustomRestEnabledLaunchSecurityTest {

}
