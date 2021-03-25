package edu.iu.uits.lms.reports.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RestReportListing extends ReportListing, but changes the boolean field to Boolean so that the controller can check for
 * null on the update
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RestReportListing extends ReportListing {
   private Boolean sisCoursesOnly;
   private Boolean newWindow;
}
