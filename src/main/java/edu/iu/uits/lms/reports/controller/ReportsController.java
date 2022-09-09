package edu.iu.uits.lms.reports.controller;

/**
 * Created by chmaurer on 9/25/15.
 */

import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.common.session.CourseSessionService;
import edu.iu.uits.lms.common.variablereplacement.MacroVariableMapper;
import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.lti.LTIConstants;
import edu.iu.uits.lms.lti.controller.OidcTokenAwareController;
import edu.iu.uits.lms.lti.service.OidcTokenUtils;
import edu.iu.uits.lms.reports.ReportConstants;
import edu.iu.uits.lms.reports.ReportsException;
import edu.iu.uits.lms.reports.handler.RosterStatusReportHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ox.ctl.lti13.security.oauth2.client.lti.authentication.OidcAuthenticationToken;

import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("/app")
@Slf4j
public class ReportsController extends OidcTokenAwareController {

    @Autowired
    private CourseService courseService = null;

    @Autowired
    private RosterStatusReportHandler rosterStatusReportHandler = null;

    @Autowired
    private ResourceBundleMessageSource messageSource = null;

    @Autowired
    private CourseSessionService courseSessionService = null;

    @Autowired
    private VariableReplacementService variableReplacementService = null;

    @RequestMapping(value = "/launch")
    @Secured(LTIConstants.BASE_USER_AUTHORITY)
    public ModelAndView launch(Model model, HttpSession httpSession) {
        OidcAuthenticationToken token = getTokenWithoutContext();

        OidcTokenUtils oidcTokenUtils = new OidcTokenUtils(token);
        String courseId = oidcTokenUtils.getCourseId();

        String reportCode = oidcTokenUtils.getCustomValue("lms_report_location");

        String pathIdentifier = reportCode == null ? "aggregator" : reportCode;

//        courseSessionService.addAttributeToSession(httpSession, courseId, ReportConstants.ROLES_DATA_KEY, Arrays.asList(oidcTokenUtils.getCustomCanvasMembershipRoles()));
        courseSessionService.addAttributeToSession(httpSession, courseId, ReportConstants.VARIABLE_REPLACEMENT_DATA_KEY, setupMacroVariableReplacement(token));

        return new ModelAndView("redirect:/app/" + courseId + "/" + pathIdentifier);
    }

    @RequestMapping("/{courseId}/rosterStatus")
    @Secured(LTIConstants.INSTRUCTOR_AUTHORITY)
    public String rosterStatusReport(@PathVariable("courseId") String courseId, Model model) {
        Course course = courseService.getCourse(courseId);
        String courseCode = course.getCourseCode();

        model.addAttribute("courseCode", courseCode);
        model.addAttribute("headers", rosterStatusReportHandler.getReportHeaders());
        try {
            model.addAttribute("data", rosterStatusReportHandler.getReportData(courseId));
        } catch (ReportsException e) {
            model.addAttribute("error", messageSource.getMessage("canvasDataError", null, Locale.getDefault()));
        }

        return "rosterStatus";
    }

    @RequestMapping(value = "/{courseId}/aggregator")
    @Secured(LTIConstants.BASE_USER_AUTHORITY)
    public String index(@PathVariable("courseId") String courseId, Model model, HttpSession httpSession) {
        OidcAuthenticationToken token = getValidatedToken(courseId);
        model.addAttribute("courseId", courseId);

        //For session tracking
        model.addAttribute("customId", httpSession.getId());
        return "react";
    }

    @RequestMapping(value = "/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }


    protected MacroVariableMapper setupMacroVariableReplacement(OidcAuthenticationToken token) {
        OidcTokenUtils oidcTokenUtils = new OidcTokenUtils(token);


        MacroVariableMapper macroVariableMapper = new MacroVariableMapper();

        macroVariableMapper.setUserLastName(oidcTokenUtils.getPersonFamilyName());
        macroVariableMapper.setUserFirstName(oidcTokenUtils.getPersonGivenName());
        macroVariableMapper.setUserNetworkId(oidcTokenUtils.getUserLoginId());
        macroVariableMapper.setUserId(oidcTokenUtils.getSisUserId());

        String[] roles = oidcTokenUtils.getCustomCanvasMembershipRoles();

        macroVariableMapper.setCanvasCourseId(oidcTokenUtils.getCourseId());

        variableReplacementService.setupMapper(macroVariableMapper, roles);

        return macroVariableMapper;
    }


}
