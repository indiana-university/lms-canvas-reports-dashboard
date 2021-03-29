package edu.iu.uits.lms.reports.handler;

import edu.iu.uits.lms.reports.ReportsException;
import iuonly.client.generated.api.CanvasDataApi;
import iuonly.client.generated.model.Enrollment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by chmaurer on 9/30/15.
 */
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
    private CanvasDataApi canvasDataService = null;


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
        } catch (RestClientException e) {
            throw new ReportsException(e);
        }
    }

}
