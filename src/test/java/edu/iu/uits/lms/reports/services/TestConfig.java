package edu.iu.uits.lms.reports.services;

import edu.iu.uits.lms.iuonly.services.CanvasDataServiceImpl;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class TestConfig {
    @MockBean
    private ReportListingRepository reportListingRepository;

    @MockBean
    private CanvasDataServiceImpl canvasDataService;
}
