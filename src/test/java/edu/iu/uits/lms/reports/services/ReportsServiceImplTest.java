package edu.iu.uits.lms.reports.services;

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.model.Course;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.reports.config.ToolConfig;
import edu.iu.uits.lms.reports.handler.RosterStatusReportHandler;
import edu.iu.uits.lms.reports.model.DecoratedReport;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import edu.iu.uits.lms.reports.service.ReportsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestExecutionListeners(value = { DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class },
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@Slf4j
@ActiveProfiles("none")
@DatabaseSetup(value = "classpath:reports.xml")
public class ReportsServiceImplTest {

   @Autowired
   private ReportsService reportsService;

   @MockBean
   private CoursesApi coursesApi;

   @MockBean
   private ToolConfig toolConfig;

   @Autowired
   private ReportListingRepository reportListingRepository;

   @MockBean
   private VariableReplacementService variableReplacementService;

   @MockBean
   private RosterStatusReportHandler rosterStatusReportHandler;


   @Before
   public void setUp() throws Exception {

      Course course1 = createCourse("12345", "sis_12345");
      Course course2 = createCourse("54321", null);

      Mockito.when(coursesApi.getCourse(course1.getId())).thenReturn(course1);
      Mockito.when(coursesApi.getCourse(course2.getId())).thenReturn(course2);
   }

   private Course createCourse(String courseId, String sisCourseId) {
      Course course = new Course();
      course.setId(courseId);
      course.setSisCourseId(sisCourseId);

      return course;
   }

   @Test
   @DatabaseTearDown(type = DatabaseOperation.DELETE_ALL)
   public void testReturnAllReports() {
      List<String> roles = Arrays.asList("Instructor", "Student", "Designer");
      List<DecoratedReport> reports = reportsService.getReportsToDisplay("12345", roles, null);

      Assert.assertNotNull(reports);
      Assert.assertEquals(3, reports.size());
   }

   @Test
   @DatabaseTearDown(type = DatabaseOperation.DELETE_ALL)
   public void testReturnSisReports() {
      List<String> roles = Arrays.asList("Instructor", "Student", "Designer");
      List<DecoratedReport> reports = reportsService.getReportsToDisplay("54321", roles, null);

      Assert.assertNotNull(reports);
      Assert.assertEquals(1, reports.size());
   }

   @TestConfiguration
   static class TestContextConfiguration {
      @Bean
      public ReportsService reportsService() {
         return new ReportsService();
      }
   }
}
