package edu.iu.uits.lms.reports.handler;

/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2025 Indiana University
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
