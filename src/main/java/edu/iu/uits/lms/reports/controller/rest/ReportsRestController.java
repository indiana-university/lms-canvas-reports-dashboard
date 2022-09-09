package edu.iu.uits.lms.reports.controller.rest;

import edu.iu.uits.lms.reports.model.ReportListing;
import edu.iu.uits.lms.reports.model.RestReportListing;
import edu.iu.uits.lms.reports.repository.ReportListingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

   @PostMapping(value = "/{id}/images")
   @Operation(summary = "Update the images of an existing ReportListing by id")
   public ResponseEntity updateImages(@PathVariable Long id, @RequestParam(value = "reportImage", required = false) MultipartFile reportImage,
                                      @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) {
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
