package edu.iu.uits.lms.reports.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created by chmaurer on 11/18/15.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportHeader {
    @NonNull
    private String text;
    private String cssClass;
}
