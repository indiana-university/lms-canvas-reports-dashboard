package edu.iu.uits.lms.reports;

/**
 * Created by chmaurer on 5/20/16.
 */
public class ReportsException extends Exception {

    public ReportsException() {
        super();
    }

    public ReportsException(Throwable cause) {
        super(cause);
    }

    public ReportsException(String message) {
        super(message);
    }

    public ReportsException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ReportsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
