package edu.iu.uits.lms.reports.services;

import canvas.client.generated.api.CoursesApi;
import edu.iu.uits.lms.common.session.CourseSessionService;
import edu.iu.uits.lms.lti.security.LtiAuthenticationProvider;
import edu.iu.uits.lms.lti.security.LtiAuthenticationToken;
import edu.iu.uits.lms.reports.config.ToolConfig;
import edu.iu.uits.lms.reports.controller.ReportsController;
import edu.iu.uits.lms.reports.handler.RosterStatusReportHandler;
import edu.iu.uits.lms.reports.service.ReportsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReportsController.class)
@Import(ToolConfig.class)
@ActiveProfiles("none")
public class AppLaunchSecurityTest {

   @Autowired
   private MockMvc mvc;

   @MockBean
   private CoursesApi courseService = null;

   @MockBean
   private RosterStatusReportHandler rosterStatusReportHandler = null;

   @MockBean
   private ResourceBundleMessageSource messageSource = null;

   @MockBean
   private ReportsService reportsService = null;

   @MockBean
   private CourseSessionService courseSessionService;

   @Test
   public void appNoAuthnLaunch() throws Exception {
      //This is a secured endpoint and should not not allow access without authn
      mvc.perform(get("/app/1234/aggregator")
            .header(HttpHeaders.USER_AGENT, TestUtils.defaultUseragent())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
   }

   @Test
   public void appAuthnWrongContextLaunch() throws Exception {
      LtiAuthenticationToken token = new LtiAuthenticationToken("userId",
            "asdf", "systemId",
            AuthorityUtils.createAuthorityList(LtiAuthenticationProvider.LTI_USER_ROLE, "authority"),
            "unit_test");

      SecurityContextHolder.getContext().setAuthentication(token);

      //This is a secured endpoint and should not not allow access without authn
      ResultActions mockMvcAction = mvc.perform(get("/app/1234/aggregator")
              .header(HttpHeaders.USER_AGENT, TestUtils.defaultUseragent())
              .contentType(MediaType.APPLICATION_JSON));

      mockMvcAction.andExpect(status().isInternalServerError());
      mockMvcAction.andExpect(MockMvcResultMatchers.view().name ("error"));
      mockMvcAction.andExpect(MockMvcResultMatchers.model().attributeExists("error"));
   }

   @Test
   public void appAuthnLaunch() throws Exception {
      LtiAuthenticationToken token = new LtiAuthenticationToken("userId",
            "1234", "systemId",
            AuthorityUtils.createAuthorityList(LtiAuthenticationProvider.LTI_USER_ROLE, "authority"),
            "unit_test");

      SecurityContextHolder.getContext().setAuthentication(token);

      //This is a secured endpoint and should not not allow access without authn
      mvc.perform(get("/app/1234/aggregator")
            .header(HttpHeaders.USER_AGENT, TestUtils.defaultUseragent())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
   }

   @Test
   public void randomUrlNoAuth() throws Exception {
      //This is a secured endpoint and should not not allow access without authn
      mvc.perform(get("/asdf/foobar")
            .header(HttpHeaders.USER_AGENT, TestUtils.defaultUseragent())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
   }

   @Test
   public void randomUrlWithAuth() throws Exception {
      LtiAuthenticationToken token = new LtiAuthenticationToken("userId",
            "1234", "systemId",
            AuthorityUtils.createAuthorityList(LtiAuthenticationProvider.LTI_USER_ROLE, "authority"),
            "unit_test");
      SecurityContextHolder.getContext().setAuthentication(token);

      //This is a secured endpoint and should not not allow access without authn
      mvc.perform(get("/asdf/foobar")
            .header(HttpHeaders.USER_AGENT, TestUtils.defaultUseragent())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
   }
}
