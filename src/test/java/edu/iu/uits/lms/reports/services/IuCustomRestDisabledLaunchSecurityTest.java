package edu.iu.uits.lms.reports.services;

import edu.iu.uits.lms.iuonly.AbstractIuCustomRestDisabledLaunchSecurityTest;
import edu.iu.uits.lms.reports.config.ToolConfig;
import org.springframework.context.annotation.Import;

@Import({ToolConfig.class, TestConfig.class})
public class IuCustomRestDisabledLaunchSecurityTest extends AbstractIuCustomRestDisabledLaunchSecurityTest {

}
