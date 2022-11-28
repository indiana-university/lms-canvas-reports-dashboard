package edu.iu.uits.lms.reports.controller;

/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
import org.apache.commons.lang3.StringUtils;
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

    @RequestMapping("/{courseId}/roleInspector")
    @Secured(LTIConstants.BASE_USER_AUTHORITY)
    public String roleInspector(@PathVariable("courseId") String courseId, Model model) {
        OidcAuthenticationToken token = getValidatedToken(courseId);
        OidcTokenUtils oidcTokenUtils = new OidcTokenUtils(token);

        String[] roles = oidcTokenUtils.getRoles();
        String[] membershipRoles = StringUtils.split(oidcTokenUtils.getCustomValue("membership_role"), ",");
        String[] instructureMembershipRolesRaw = oidcTokenUtils.getCustomInstructureMembershipRolesRaw();
        String[] instructureMembershipRoles = oidcTokenUtils.getCustomInstructureMembershipRoles();
        String[] canvasMembershipRoles = oidcTokenUtils.getCustomCanvasMembershipRoles();

        model.addAttribute("userLoginId", oidcTokenUtils.getUserLoginId());
        model.addAttribute("roles", roles);
        model.addAttribute("membershipRoles", membershipRoles);
        model.addAttribute("instructureMembershipRoles", instructureMembershipRolesRaw);
        model.addAttribute("canvasMembershipRoles", canvasMembershipRoles);

        model.addAttribute("resolvedRoles", instructureMembershipRoles);

        return "roleInspector";
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
