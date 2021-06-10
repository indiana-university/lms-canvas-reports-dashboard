package edu.iu.uits.lms.reports.services;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import edu.iu.uits.lms.reports.config.ToolConfig;
import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestExecutionListeners(value = { DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class },
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Slf4j
@ActiveProfiles("none")
public class ReportListingRepositoryTest {

   @Autowired
   private ReportListingRepository reportListingRepository;

   @MockBean
   private ToolConfig toolConfig;

   @Test
   @DatabaseSetup(value = "/reports.xml")
   @DatabaseTearDown(type = DatabaseOperation.DELETE_ALL)
   public void testRead() throws Exception {
      ReportListing rl = reportListingRepository.findById(1L).orElse(null);
      Assert.assertNotNull(rl);
      Assert.assertEquals("title doesn't match", "Report 1", rl.getTitle());
      Assert.assertEquals(1, rl.getAllowedRoles().size());

      List<ReportListing> reports = (List<ReportListing>)reportListingRepository.findAll();
      Assert.assertNotNull(reports);
      Assert.assertEquals(3, reports.size());

      List<ReportListing> reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(Collections.singletonList("Student"));
      Assert.assertNotNull(reportsByRole);
      Assert.assertEquals(1, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(Collections.singletonList("Instructor"));
      Assert.assertNotNull(reportsByRole);
      Assert.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(Arrays.asList("Instructor", "Student"));
      Assert.assertNotNull(reportsByRole);
      Assert.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(Arrays.asList("Instructor", "Student", "Other"));
      Assert.assertNotNull(reportsByRole);
      Assert.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(Collections.singletonList("None"));
      Assert.assertNotNull(reportsByRole);
      Assert.assertEquals(0, reportsByRole.size());
   }
}
