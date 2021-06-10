package edu.iu.uits.lms.reports.controller;

import edu.iu.uits.lms.common.session.CourseSessionService;
import edu.iu.uits.lms.common.variablereplacement.MacroVariableMapper;
import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.lti.controller.LtiController;
import edu.iu.uits.lms.lti.security.LtiAuthenticationProvider;
import edu.iu.uits.lms.lti.security.LtiAuthenticationToken;
import edu.iu.uits.lms.reports.ReportConstants;
import edu.iu.uits.lms.reports.config.ToolConfig;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tsugi.basiclti.BasicLTIConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chmaurer on 6/15/15.
 */
@Controller
@RequestMapping("/lti")
@Slf4j
public class ReportsLtiController extends LtiController {

    @Autowired
    private VariableReplacementService variableReplacementService = null;

    @Autowired
    private CourseSessionService courseSessionService;

    @Autowired
    private ToolConfig toolConfig;

    private static final String CUSTOM_REPORT_CODE = "custom_lms_report_location";

    private boolean openLaunchUrlInNewWindow = false;

    @Override
    protected String getLaunchUrl(Map<String, String> launchParams) {
        String reportCode = launchParams.get(CUSTOM_REPORT_CODE);
        String courseId = launchParams.get(CUSTOM_CANVAS_COURSE_ID);

        //If it's not a specific report, go to the aggregator
        String pathIdentifier = reportCode == null ? "aggregator" : reportCode;
        return "/app/" + courseId + "/" + pathIdentifier;
    }

    private List<String> getLaunchParamList() {
        return Arrays.asList(CUSTOM_CANVAS_COURSE_ID, CUSTOM_REPORT_CODE,
              CUSTOM_CANVAS_USER_ID, CUSTOM_CANVAS_USER_LOGIN_ID, BasicLTIConstants.LIS_PERSON_NAME_FAMILY,
              BasicLTIConstants.LIS_PERSON_NAME_GIVEN, BasicLTIConstants.LIS_PERSON_SOURCEDID, BasicLTIConstants.ROLES);
    }

    @Override
    protected Map<String, String> getParametersForLaunch(Map<String, String> payload, Claims claims) {
        Map<String, String> paramMap = new HashMap<>(1);

        for (String prop : getLaunchParamList()) {
            paramMap.put(prop, payload.get(prop));
        }

        openLaunchUrlInNewWindow = Boolean.valueOf(payload.get(CUSTOM_OPEN_IN_NEW_WINDOW));

        return paramMap;
    }

    @Override
    protected void preLaunchSetup(Map<String, String> launchParams, HttpServletRequest request, HttpServletResponse response) {
        String rolesString = launchParams.get(BasicLTIConstants.ROLES);
        String[] userRoles = rolesString.split(",");
        String authority = returnEquivalentAuthority(Arrays.asList(userRoles), toolConfig.getInstructorRoles());
        log.debug("LTI equivalent authority: " + authority);

        String userId = launchParams.get(CUSTOM_CANVAS_USER_LOGIN_ID);
        String systemId = launchParams.get(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_GUID);
        String courseId = launchParams.get(CUSTOM_CANVAS_COURSE_ID);

        courseSessionService.addAttributeToSession(request.getSession(), courseId, ReportConstants.ROLES_DATA_KEY, Arrays.asList(userRoles));
        courseSessionService.addAttributeToSession(request.getSession(), courseId, ReportConstants.VARIABLE_REPLACEMENT_DATA_KEY, setupMacroVariableReplacement(launchParams));

        LtiAuthenticationToken token = new LtiAuthenticationToken(userId,
                courseId, systemId, AuthorityUtils.createAuthorityList(LtiAuthenticationProvider.LTI_USER_ROLE, authority), getToolContext());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    protected MacroVariableMapper setupMacroVariableReplacement(Map<String, String> launchParams) {
        MacroVariableMapper macroVariableMapper = new MacroVariableMapper();

        macroVariableMapper.setUserLastName(launchParams.get(BasicLTIConstants.LIS_PERSON_NAME_FAMILY));
        macroVariableMapper.setUserFirstName(launchParams.get(BasicLTIConstants.LIS_PERSON_NAME_GIVEN));
        macroVariableMapper.setUserNetworkId(launchParams.get(CUSTOM_CANVAS_USER_LOGIN_ID));
        macroVariableMapper.setUserId(launchParams.get(BasicLTIConstants.LIS_PERSON_SOURCEDID));

        String extRoles = launchParams.get(BasicLTIConstants.ROLES);
        String[] roles = extRoles.split(",");

        String canvasCourseId = launchParams.get(CUSTOM_CANVAS_COURSE_ID);
        macroVariableMapper.setCanvasCourseId(canvasCourseId);

        variableReplacementService.setupMapper(macroVariableMapper, roles);

        return macroVariableMapper;
    }

    @Override
    protected String getToolContext() {
        return "lms_reports";
    }

    @Override
    protected LAUNCH_MODE launchMode() {
        if (openLaunchUrlInNewWindow) {
            return LAUNCH_MODE.WINDOW;
        }
        return LAUNCH_MODE.FORWARD;
    }
}
