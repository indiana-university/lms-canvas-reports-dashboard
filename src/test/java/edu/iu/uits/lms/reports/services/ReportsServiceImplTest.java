package edu.iu.uits.lms.reports.services;

import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.reports.config.PostgresDBConfig;
import edu.iu.uits.lms.reports.config.ToolConfig;
import edu.iu.uits.lms.reports.model.DecoratedReport;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import edu.iu.uits.lms.reports.service.ReportsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@Import({ToolConfig.class, PostgresDBConfig.class})
@Sql("/reports.sql")
@Slf4j
@ActiveProfiles("reports")
public class ReportsServiceImplTest {

   @Autowired
   private ReportsService reportsService;

   @MockBean
   private CourseService courseService;

   @Autowired
   private ReportListingRepository reportListingRepository;

   @BeforeEach
   public void setUp() throws Exception {

      Course course1 = createCourse("12345", "sis_12345");
      Course course2 = createCourse("54321", null);

      Mockito.when(courseService.getCourse(course1.getId())).thenReturn(course1);
      Mockito.when(courseService.getCourse(course2.getId())).thenReturn(course2);

      //initialize data

   }

   private Course createCourse(String courseId, String sisCourseId) {
      Course course = new Course();
      course.setId(courseId);
      course.setSisCourseId(sisCourseId);

      return course;
   }

   @Test
   public void testReturnAllReports() {
      String[] roles = new String[] {"TeacherEnrollment", "StudentEnrollment", "DesignerEnrollment"};
      List<DecoratedReport> reports = reportsService.getReportsToDisplay("12345", roles, null);

      Assertions.assertNotNull(reports);
      Assertions.assertEquals(3, reports.size());
   }

   @Test
   public void testReturnSisReports() {
      String[] roles = new String[] {"TeacherEnrollment", "StudentEnrollment", "DesignerEnrollment"};
      List<DecoratedReport> reports = reportsService.getReportsToDisplay("54321", roles, null);

      Assertions.assertNotNull(reports);
      Assertions.assertEquals(1, reports.size());
   }

   @TestConfiguration
   static class TestContextConfiguration {
      @Bean
      public ReportsService reportsService() {
         return new ReportsService();
      }
   }
}
