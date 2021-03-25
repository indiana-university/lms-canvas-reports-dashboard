package edu.iu.uits.lms.reports.model;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

@Data
public class DecoratedReport {

   private Long id;
   private String title;
   private String description;
   private String url;
   private String thumbnail;
   private String image;
   private String contact;
   private String kbUrl;
   private boolean newWindow;

   public DecoratedReport(ReportListing reportListing, String processedUrl) {
      this.id = reportListing.getId();
      this.title = reportListing.getTitle();
      this.description = reportListing.getDescription();
      this.thumbnail = encodeImage(reportListing.getThumbnail());
      this.image = encodeImage(reportListing.getImage());
      this.contact = reportListing.getContact();
      this.url = processedUrl;
      this.kbUrl = reportListing.getKbUrl();
      this.newWindow = reportListing.isNewWindow();
   }

   private String encodeImage(byte[] image) {
      return "data:image/jpeg;base64," + Base64.encodeBase64String(image);
   }
}
