package edu.iu.uits.lms.reports.services;

import edu.iu.uits.lms.reports.config.PostgresDBConfig;
import edu.iu.uits.lms.reports.config.ToolConfig;
import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@Import({ToolConfig.class, PostgresDBConfig.class})
@Sql("/reports.sql")
@Slf4j
@ActiveProfiles("reports")
public class ReportListingRepositoryTest {

   @Autowired
   private ReportListingRepository reportListingRepository;

   @Test
   public void testRead() throws Exception {
      ReportListing rl = reportListingRepository.findById(1L).orElse(null);
      Assertions.assertNotNull(rl);
      Assertions.assertEquals("Report 1", rl.getTitle(), "title doesn't match");
      Assertions.assertEquals(1, rl.getAllowedRoles().size());

      List<ReportListing> reports = (List<ReportListing>)reportListingRepository.findAll();
      Assertions.assertNotNull(reports);
      Assertions.assertEquals(3, reports.size());

      List<ReportListing> reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(new String[]{"StudentEnrollment"});
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(1, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(new String[]{"TeacherEnrollment"});
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(new String[]{"TeacherEnrollment", "StudentEnrollment"});
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(new String[]{"TeacherEnrollment", "StudentEnrollment", "Other"});
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(new String[]{"None"});
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(0, reportsByRole.size());
   }
}
