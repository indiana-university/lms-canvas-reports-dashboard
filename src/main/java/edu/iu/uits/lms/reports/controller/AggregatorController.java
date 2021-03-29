package edu.iu.uits.lms.reports.controller;

import edu.iu.uits.lms.common.session.CourseSessionService;
import edu.iu.uits.lms.common.variablereplacement.MacroVariableMapper;
import edu.iu.uits.lms.lti.security.LtiAuthenticationToken;
import edu.iu.uits.lms.reports.ReportConstants;
import edu.iu.uits.lms.reports.model.DecoratedReport;
import edu.iu.uits.lms.reports.service.ReportsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This controller is for internal use only by the application and doesn't need to be exposed externally with swagger docs
 */
@RestController
@RequestMapping("/app/rest")
@Slf4j
public class AggregatorController extends ReportsController {

   @Autowired
   private ReportsService reportsService = null;

   @Autowired
   private CourseSessionService courseSessionService;

   @GetMapping("/{courseId}/byRoles")
   public List<DecoratedReport> getReportsByRole(@PathVariable("courseId") String courseId, HttpSession session) {
      log.debug("in /app/rest/" + courseId + "/byRoles");
      LtiAuthenticationToken token = getValidatedToken(courseId);
      List<String> roles = courseSessionService.getAttributeFromSession(session, courseId, ReportConstants.ROLES_DATA_KEY, List.class);
      MacroVariableMapper mapper = courseSessionService.getAttributeFromSession(session, courseId, ReportConstants.VARIABLE_REPLACEMENT_DATA_KEY, MacroVariableMapper.class);

      List<DecoratedReport> decoratedReports = reportsService.getReportsToDisplay(courseId, roles, mapper);

      return decoratedReports;
   }

   @Data
   public static class RoleWrapper {
      private List<String> roles;
   }
}
