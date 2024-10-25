package edu.iu.uits.lms.reports.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDetails implements Serializable {

    private String canvasUserId;
    private String userId;
    private String loginId;
    private String firstName;
    private String lastName;
    private String primaryEmail;
    private String pseudonymId;
    private String uniqueId;
    private Date createdAt;
    private Date updatedAt;
    private Date lastRequestAt;

}
