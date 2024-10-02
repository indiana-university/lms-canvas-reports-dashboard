package edu.iu.uits.lms.reports.model;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.iu.uits.lms.common.date.DateFormatUtil;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

   @Column(name = "allowed_group")
   @ElementCollection(fetch = FetchType.EAGER)
   @CollectionTable(name = "LMS_REPORT_GROUPS", joinColumns = @JoinColumn(name = "report_id"),
           foreignKey = @ForeignKey(name = "FK_report_group"))
   @EqualsAndHashCode.Exclude
   private List<String> allowedGroups;

   @Column(name = "allowed_course")
   @ElementCollection(fetch = FetchType.EAGER)
   @CollectionTable(name = "LMS_REPORT_COURSES", joinColumns = @JoinColumn(name = "report_id"),
           foreignKey = @ForeignKey(name = "FK_report_course"))
   @EqualsAndHashCode.Exclude
   private List<String> canvasCourseIds;

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
