package edu.iu.uits.lms.reports.handler;

import edu.iu.uits.lms.iuonly.exceptions.CanvasDataServiceException;
import edu.iu.uits.lms.iuonly.services.CanvasDataServiceImpl;
import edu.iu.uits.lms.reports.ReportsException;
import edu.iu.uits.lms.reports.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class DefaultEmailReportHandler {

    private static String[] headerKeys = {
            "defaultEmail.report.header.canvasUserId",
            "defaultEmail.report.header.userId",
            "defaultEmail.report.header.loginId",
            "defaultEmail.report.header.firstName",
            "defaultEmail.report.header.lastName",
            "defaultEmail.report.header.primaryEmail",
            "defaultEmail.report.header.pseudonymId",
            "defaultEmail.report.header.uniqueId",
            "defaultEmail.report.header.createdAt",
            "defaultEmail.report.header.updatedAt",
            "defaultEmail.report.header.lastRequestAt"};

    @Autowired
    private ResourceBundleMessageSource messageBundle = null;

    @Autowired
    private CanvasDataServiceImpl canvasDataService = null;


    public List<String> getReportHeaders() {
        List<String> headers = new ArrayList<>();

        for (String headerKey : headerKeys) {
            headers.add(messageBundle.getMessage(headerKey, null, Locale.getDefault()));
        }

        return headers;
    }

    /*
    SELECT u.canvas_user_id AS "Canvas UserID", u.user_id AS "SIS EmplID", u.login_id AS "Login ID", u.first_name AS "First Name",
    u.last_name AS "Last Name", u.email AS "primary_email", p.pseudonym_id AS "Pseudonym ID", p.unique_id AS "Unique ID,
    p.created_at AS "Pseudo Created", p.updated_at AS "Pseudo Updated",  p.last_request_at AS "Last Request"
FROM canvas_warehouse.api_canvas_users u
INNER JOIN iu_la.cd_pseudonyms_flt p ON (u.canvas_user_id = p.user_id)
WHERE ((u.user_id LIKE '0%') OR (u.user_id LIKE '2%'))
AND u.user_id NOT LIKE '%@%'
AND u.email NOT LIKE '%@iu.edu'
AND u.status != 'deleted'
AND p.workflow_state != 'deleted';
     */

    public  List<UserDetails> getReportData() throws ReportsException {
        String sql = """
            SELECT  cast(u.canvas_user_id as varchar) AS canvas_user_id,
                    cast(u.user_id as varchar) AS user_id,
                    u.login_id,
                    u.first_name,
                    u.last_name,
                    u.email AS "primary_email",
                    cast(p.pseudonym_id as varchar) AS pseudonym_id,
                    cast (p.unique_id as varchar) AS unique_id,
                    p.created_at,
                    p.updated_at,
                    p.last_request_at
            FROM canvas_warehouse.api_canvas_users u
            INNER JOIN iu_la.cd_pseudonyms_flt p
                ON (u.canvas_user_id = p.user_id)
            WHERE ((u.user_id LIKE '0%') OR (u.user_id LIKE '2%'))
                AND u.user_id NOT LIKE '%@%'
                AND u.email NOT LIKE '%@iu.edu'
                AND u.status != 'deleted'
                AND p.workflow_state != 'deleted'
            """;
        try {
            return canvasDataService.getSqlResults(sql, UserDetails.class);
        } catch (CanvasDataServiceException e) {
            throw new ReportsException(e);
        }
    }
}
