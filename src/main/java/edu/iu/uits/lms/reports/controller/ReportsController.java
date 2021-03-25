package edu.iu.uits.lms.reports.controller;

/**
 * Created by chmaurer on 9/25/15.
 */

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.model.Course;
import edu.iu.uits.lms.lti.LTIConstants;
import edu.iu.uits.lms.lti.controller.LtiAuthenticationTokenAwareController;
import edu.iu.uits.lms.lti.security.LtiAuthenticationProvider;
import edu.iu.uits.lms.lti.security.LtiAuthenticationToken;
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

import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@RequestMapping("/app")
@Slf4j
public class ReportsController extends LtiAuthenticationTokenAwareController {

    @Autowired
    private CoursesApi courseService = null;

    @Autowired
    private RosterStatusReportHandler rosterStatusReportHandler = null;

    @Autowired
    private ResourceBundleMessageSource messageSource = null;

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
    @Secured({LtiAuthenticationProvider.LTI_USER_ROLE})
    public String index(@PathVariable("courseId") String courseId, Model model, HttpSession httpSession) {
        LtiAuthenticationToken token = getValidatedToken(courseId);
        model.addAttribute("courseId", courseId);

        //For session tracking
        model.addAttribute("customId", httpSession.getId());
        return "react";
    }

    @RequestMapping(value = "/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }


}
