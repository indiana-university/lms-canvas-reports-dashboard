package edu.iu.uits.lms.reports.service;

import canvas.client.generated.api.CoursesApi;
import canvas.client.generated.model.Course;
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
   private CoursesApi coursesApi = null;

   public List<DecoratedReport> getReportsToDisplay(String courseId, List<String> roles, MacroVariableMapper mapper) {
      Course course = coursesApi.getCourse(courseId);

      //Filter for the roles we want
      List<ReportListing> reportListings = reportListingRepository.findDistinctByAllowedRolesInOrderByTitleAsc(roles);

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
