package edu.iu.uits.lms.reports.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.iu.uits.lms.common.date.DateFormatUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "LMS_REPORTS")
@SequenceGenerator(name = "LMS_REPORTS_ID_SEQ", sequenceName = "LMS_REPORTS_ID_SEQ", allocationSize = 1)
@Data
@NoArgsConstructor
public class ReportListing {

   @Id
   @GeneratedValue(generator = "LMS_REPORTS_ID_SEQ")
   private Long id;
   private String title;

   @Column(length = 4000)
   private String description;

   @Column(length = 4000)
   private String url;
   private byte[] thumbnail;
   private byte[] image;

   @Column(name = "allowed_role")
   @ElementCollection(fetch = FetchType.EAGER)
   @CollectionTable(name = "LMS_REPORT_ROLES", joinColumns = @JoinColumn(name = "report_id"),
         foreignKey = @ForeignKey(name = "FK_report_role"))
   @EqualsAndHashCode.Exclude
   private List<String> allowedRoles;
   private String contact;

   @Column(name = "sis_courses_only")
   private boolean sisCoursesOnly;

   @Column(length = 4000, name = "kburl")
   private String kbUrl;

   @JsonFormat(pattern= DateFormatUtil.JSON_DATE_FORMAT)
   @Column(name = "createdon")
   private Date createdOn;
   @JsonFormat(pattern= DateFormatUtil.JSON_DATE_FORMAT)
   @Column(name = "modifiedon")
   private Date modifiedOn;

   @Column(name = "new_window")
   // default to true
   private boolean newWindow = true;


   @PreUpdate
   @PrePersist
   public void updateTimeStamps() {
      modifiedOn = new Date();
      if (createdOn==null) {
         createdOn = new Date();
      }
   }

}
