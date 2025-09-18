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
import edu.iu.uits.lms.iuonly.model.Enrollment;
import edu.iu.uits.lms.iuonly.services.CanvasDataServiceImpl;
import edu.iu.uits.lms.reports.ReportsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Slf4j
@Component
public class RosterStatusReportHandler {

    private static String[] headerKeys = {
            "rosterStatus.report.header.name",
            "rosterStatus.report.header.username",
            "rosterStatus.report.header.role",
            "rosterStatus.report.header.section",
            "rosterStatus.report.header.status",
            "rosterStatus.report.header.dateAdded",
            "rosterStatus.report.header.lastUpdated"};

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

    public  List<Enrollment> getReportData(String canvasCourseId) throws ReportsException {
        try {
            return canvasDataService.getRosterStatusInfo(canvasCourseId);
        } catch (CanvasDataServiceException e) {
            throw new ReportsException(e);
        }
    }

}
