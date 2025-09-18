package edu.iu.uits.lms.reports.controller.rest;

/*-
 * #%L
 * reports
 * %%
 * Copyright (C) 2015 - 2025 Indiana University
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

import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.model.RestReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/rest")
@Tag(name = "ReportsRestController", description = "Interact with the LMS_REPORTS repository with CRUD operations")
@Slf4j
public class ReportsRestController {

   @Autowired
   private ReportListingRepository reportListingRepository;

   @GetMapping("/{id}")
   @Operation(summary = "Get a ReportListing by id")
   public ReportListing getReportFromId(@PathVariable Long id) {
      return reportListingRepository.findById(id).orElse(null);
   }

   @GetMapping("/all")
   @Operation(summary = "Get all ReportListing records")
   public List<ReportListing> getAll() {
      List<ReportListing> reports = (List<ReportListing>) reportListingRepository.findAll();
      reports.forEach(r -> {
         r.setImage(null);
         r.setThumbnail(null);
      });
      return reports;
   }

   @PutMapping(value = "/{id}")
   @Operation(summary = "Update an existing ReportListing by id")
   public ResponseEntity updateReportListing(@PathVariable Long id, @RequestBody RestReportListing reportListing) {
      ReportListing updated = reportListingRepository.findById(id).orElse(null);

      if (reportListing.getTitle() != null) {
         updated.setTitle(reportListing.getTitle());
      }

      if (reportListing.getDescription() != null) {
         updated.setDescription(reportListing.getDescription());
      }

      if (reportListing.getUrl() != null) {
         updated.setUrl(reportListing.getUrl());
      }

      if (reportListing.getAllowedRoles() != null && !reportListing.getAllowedRoles().isEmpty()) {
         updated.setAllowedRoles(reportListing.getAllowedRoles());
      }

      if (reportListing.getAllowedGroups() != null && !reportListing.getAllowedGroups().isEmpty()) {
         updated.setAllowedGroups(reportListing.getAllowedGroups());
      }

      if (reportListing.getCanvasCourseIds() != null && !reportListing.getCanvasCourseIds().isEmpty()) {
         updated.setCanvasCourseIds(reportListing.getCanvasCourseIds());
      }

      if (reportListing.getContact() != null) {
         updated.setContact(reportListing.getContact());
      }

      if (reportListing.getSisCoursesOnly() != null) {
         updated.setSisCoursesOnly(reportListing.getSisCoursesOnly());
      }

      if (reportListing.getKbUrl() != null) {
         updated.setKbUrl(reportListing.getKbUrl());
      }

      if (reportListing.getNewWindow() != null) {
         updated.setNewWindow(reportListing.getNewWindow());
      }

      return ResponseEntity.ok(reportListingRepository.save(updated));
   }

   @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   @Operation(summary = "Update the images of an existing ReportListing by id")
   public ResponseEntity updateImages(@PathVariable Long id, @RequestPart(name = "reportImage", required = false) MultipartFile reportImage,
                                      @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail) {
      ReportListing updated = reportListingRepository.findById(id).orElse(null);

      if (reportImage != null) {
         try {
            updated.setImage(reportImage.getBytes());
         } catch (IOException e) {
            log.error("Unable to set the report image", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unable to set the report image");
         }
      }

      if (thumbnail != null) {
         try {
            updated.setThumbnail(thumbnail.getBytes());
         } catch (IOException e) {
            log.error("Unable to set the report thumbnail", e);
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unable to set the report thumbnail");
         }
      }

      return ResponseEntity.ok(reportListingRepository.save(updated));
   }

   @PostMapping("/")
   @Operation(summary = "Create a new ReportListing")
   public ResponseEntity create(@RequestBody RestReportListing reportListing) {
      ReportListing newReportListing = new ReportListing();
      newReportListing.setTitle(reportListing.getTitle());
      newReportListing.setDescription(reportListing.getDescription());
      newReportListing.setUrl(reportListing.getUrl());
      newReportListing.setAllowedRoles(reportListing.getAllowedRoles());
      newReportListing.setAllowedGroups(reportListing.getAllowedGroups());
      newReportListing.setCanvasCourseIds(reportListing.getCanvasCourseIds());
      newReportListing.setContact(reportListing.getContact());

      if (reportListing.getSisCoursesOnly() != null) {
         newReportListing.setSisCoursesOnly(reportListing.getSisCoursesOnly());
      }

      if (reportListing.getKbUrl() != null) {
         newReportListing.setKbUrl(reportListing.getKbUrl());
      }

      if (reportListing.getNewWindow() != null) {
         newReportListing.setNewWindow(reportListing.getNewWindow());
      }


      return ResponseEntity.ok(reportListingRepository.save(newReportListing));
   }

   @DeleteMapping("/{id}")
   @Operation(summary = "Delete an existing ReportListing by id")
   public String delete(@PathVariable Long id) {
      reportListingRepository.deleteById(id);
      return "Delete success.";
   }

}
