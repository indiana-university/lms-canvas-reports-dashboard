package edu.iu.uits.lms.reports.services;

/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
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

import edu.iu.uits.lms.reports.config.PostgresDBConfig;
import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@DataJpaTest
@ContextConfiguration(classes = {PostgresDBConfig.class})
@Slf4j
@ActiveProfiles("reports")
public class ReportListingRepositoryTest {

   @Autowired
   private ReportListingRepository reportListingRepository;

   @MockBean
   private JwtDecoder jwtDecoder;

   @Test
   @Sql("/reports.sql")
   public void testRead() throws Exception {
      String courseId = "12345";
      ReportListing rl = reportListingRepository.findById(1L).orElse(null);
      Assertions.assertNotNull(rl);
      Assertions.assertEquals("Report 1", rl.getTitle(), "title doesn't match");
      Assertions.assertEquals(1, rl.getAllowedRoles().size());

      List<ReportListing> reports = (List<ReportListing>)reportListingRepository.findAll();
      Assertions.assertNotNull(reports);
      Assertions.assertEquals(3, reports.size());

      List<ReportListing> reportsByRole = reportListingRepository.findAccessibleReports(new String[]{"StudentEnrollment"},
              null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(1, reportsByRole.size());

      reportsByRole = reportListingRepository.findAccessibleReports(new String[]{"TeacherEnrollment"}, null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findAccessibleReports(new String[]{"TeacherEnrollment", "StudentEnrollment"},
              null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findAccessibleReports(new String[]{"TeacherEnrollment", "StudentEnrollment", "Other"},
              null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(2, reportsByRole.size());

      reportsByRole = reportListingRepository.findAccessibleReports(new String[]{"None"}, null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(0, reportsByRole.size());

      reportsByRole = reportListingRepository.findAccessibleReports(null, null, courseId);
      Assertions.assertNotNull(reportsByRole);
      Assertions.assertEquals(0, reportsByRole.size());
   }

   @Test
   void testAccessToReportWithRoleAndCourseRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedRoles(List.of("ROLE1"));
      rl.setCanvasCourseIds(List.of("12345"));
      reportListingRepository.save(rl);

      Long reportId = rl.getId();
      // ROLE2 should not have access to report in this course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, null, false);

      // ROLE1 should have access to report in this course
      doCompare(reportId, "12345", new String[]{"ROLE1"}, null, true);

      // Someone with no role can NOT access report in this course
      doCompare(reportId, "12345", null, null, false);

      // Nothing should have access to the report from a different course
      doCompare(reportId, "99999", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, null, false);
      doCompare(reportId, "99999", null, null, false);
   }

   @Test
   void testAccessToReportWithRoleRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedRoles(List.of("ROLE1"));
      reportListingRepository.save(rl);

      Long reportId = rl.getId();
      // ROLE2 should not have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, null, false);

      // ROLE1 should have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE1"}, null, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, null, true);

      // Someone with no role can NOT access report in any course
      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);

   }

   @Test
   void testAccessToReportWithGroupAndCourseRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedGroups(List.of("GROUP1"));
      rl.setCanvasCourseIds(List.of("12345"));
      rl = reportListingRepository.save(rl);

      Long reportId = rl.getId();

      // GROUP2 should not have access to report
      doCompare(reportId, "12345",null, new String[]{"GROUP2"}, false);

      // GROUP1 should have access to report
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, true);

      // Someone with no groups can NOT access report
      doCompare(reportId, "12345", null, null, false);

      // Nothing should have access to the report from a different course
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", null, null, false);

   }

   @Test
   void testAccessToReportWithGroupRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedGroups(List.of("GROUP1"));
      rl = reportListingRepository.save(rl);

      Long reportId = rl.getId();

      // GROUP2 should not have access to report
      doCompare(reportId, "12345",null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);

      // GROUP1 should have access to report
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, true);

      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP3"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP3"}, true);
      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP2"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP2"}, true);
      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP2", "GROUP3"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP2", "GROUP3"}, true);
      doCompare(reportId, "12345", null, new String[]{"GROUP2", "GROUP4"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2", "GROUP4"}, false);

      // Someone with no groups can NOT access report
      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);

   }

   @Test
   void testAccessToReportWithNoRoleOrGroupRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl = reportListingRepository.save(rl);

      Long reportId = rl.getId();

      // No restrictions are defined, therefore there should be no access
      doCompare(reportId, "12345", new String[]{"ROLE1"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, null, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);

      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);
   }

   @Test
   void testAccessToReportWithCourseButNoRoleOrGroupRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setCanvasCourseIds(List.of("12345"));
      rl = reportListingRepository.save(rl);

      Long reportId = rl.getId();

      // No restrictions are defined, therefore there should be no access
      doCompare(reportId, "12345", new String[]{"ROLE1"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, null, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP1"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);

      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);
   }

   @Test
   void testAccessToReportWithRoleAndGroupRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedRoles(List.of("ROLE1"));
      rl.setAllowedGroups(List.of("GROUP1"));
      reportListingRepository.save(rl);

      Long reportId = rl.getId();
      // ROLE2 should not have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, null, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, null, false);

      // GROUP2 should not have access to report in any course
      doCompare(reportId, "12345", null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);

      // ROLE1 should have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE1"}, null, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, null, true);

      // GROUP1 should have access to report in any course
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, true);

      // Someone with no role or group can NOT access report in any course
      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);

      // ROLE2 or GROUP2 should not have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);

      // ROLE1 has access even though GROUP2 doesn't
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP2"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP2"}, true);

      // ROLE2 does not have access, but GROUP1 does
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP1"}, true);

      // ROLE1 has access, and so does GROUP1
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP1"}, true);
   }

   @Test
   void testAccessToReportWithEmptyRolesGroupsRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedRoles(List.of("ROLE1"));
      rl.setAllowedGroups(List.of("GROUP1"));
      reportListingRepository.save(rl);

      Long reportId = rl.getId();
      // ROLE2 should not have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[0], false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[0], false);

      // GROUP2 should not have access to report in any course
      doCompare(reportId, "12345", new String[0], new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[0], new String[]{"GROUP2"}, false);

      // ROLE1 should have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[0], true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[0], true);

      // GROUP1 should have access to report in any course
      doCompare(reportId, "12345", new String[0], new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", new String[0], new String[]{"GROUP1"}, true);

      // Someone with no role or group can NOT access report in any course
      doCompare(reportId, "12345", new String[0], new String[0], false);
      doCompare(reportId, "99999", new String[0], new String[0], false);

      // ROLE2 or GROUP2 should not have access to report in any course
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP2"}, false);

      // ROLE1 has access even though GROUP2 doesn't
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP2"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP2"}, true);

      // ROLE2 does not have access, but GROUP1 does
      doCompare(reportId, "12345", new String[]{"ROLE2"}, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE2"}, new String[]{"GROUP1"}, true);

      // ROLE1 has access, and so does GROUP1
      doCompare(reportId, "12345", new String[]{"ROLE1"}, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", new String[]{"ROLE1"}, new String[]{"GROUP1"}, true);
   }

   @Test
   void testAccessToReportWithMultipleGroupRestrictions() {
      ReportListing rl = new ReportListing();
      rl.setTitle("Test report");
      rl.setAllowedGroups(List.of("GROUP1", "GROUP3", "GROUP5"));
      rl = reportListingRepository.save(rl);

      Long reportId = rl.getId();

      // GROUP2 should not have access to report
      doCompare(reportId, "12345",null, new String[]{"GROUP2"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2"}, false);

      // GROUP1 should have access to report
      doCompare(reportId, "12345", null, new String[]{"GROUP1"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1"}, true);

      // GROUP3 should have access to report
      doCompare(reportId, "12345", null, new String[]{"GROUP3"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP3"}, true);

      // GROUP1 and GROUP3 should have access to report
      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP3"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP3"}, true);

      // GROUP1 should have access to report, even though GROUP2 does not
      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP2"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP2"}, true);

      // GROUP1 and 3 should have access to report, even though 2 does not
      doCompare(reportId, "12345", null, new String[]{"GROUP1", "GROUP2", "GROUP3"}, true);
      doCompare(reportId, "99999", null, new String[]{"GROUP1", "GROUP2", "GROUP3"}, true);

      // Neither group2 nor 4 has access to the report
      doCompare(reportId, "12345", null, new String[]{"GROUP2", "GROUP4"}, false);
      doCompare(reportId, "99999", null, new String[]{"GROUP2", "GROUP4"}, false);

      // Someone with no groups can NOT access report
      doCompare(reportId, "12345", null, null, false);
      doCompare(reportId, "99999", null, null, false);

   }

   private void doCompare(Long reportId, String courseId, String[] roles, String[] groups, boolean result) {
      List<ReportListing> reports = reportListingRepository.findAccessibleReports(roles, groups, courseId);
      Assertions.assertNotNull(reports);
      Optional<ReportListing> report = reports.stream().filter(r -> Objects.equals(reportId, r.getId())).findAny();
      Assertions.assertEquals(result, report.isPresent());
   }
}
