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
