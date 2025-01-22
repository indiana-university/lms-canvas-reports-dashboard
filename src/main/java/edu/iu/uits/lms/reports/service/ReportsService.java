package edu.iu.uits.lms.reports.service;

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

import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.common.variablereplacement.MacroVariableMapper;
import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.reports.model.DecoratedReport;
import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportsService {

   @Autowired
   private ReportListingRepository reportListingRepository = null;

   @Autowired
   private VariableReplacementService variableReplacementService = null;

   @Autowired
   private CourseService courseService = null;

   @Autowired
   private GroupService groupService;

   /**
    * Get appropriate reports
    * @param username Username that is attempting to access the reports.  Will be used for potential group lookup.
    * @param courseId Canvas course id, used to display any course-specific reports
    * @param roles List of roles for the current user, used to display any role-based reports
    * @param mapper Variable mapper
    * @return List of available reports
    */
   public List<DecoratedReport> getReportsToDisplay(String username, String courseId, String[] roles, MacroVariableMapper mapper) {
      Course course = courseService.getCourse(courseId);
      String[] groups = groupService.getGroupsForUser(username);

      //Filter for the roles/groups/courses we want
      List<ReportListing> reportListings = reportListingRepository.findAccessibleReports(roles, groups, courseId);

      //Decorate and do variable replacement
      List<DecoratedReport> decoratedReports = new ArrayList<>(reportListings.size());
      for (ReportListing originalReport : reportListings) {
         if (!originalReport.isSisCoursesOnly() || (course.getSisCourseId() != null && !course.getSisCourseId().isBlank())) {
            String newUrl = variableReplacementService.performMacroVariableReplacement(mapper, originalReport.getUrl());
            decoratedReports.add(new DecoratedReport(originalReport, newUrl));
         }
      }
      return decoratedReports;
   }
}
