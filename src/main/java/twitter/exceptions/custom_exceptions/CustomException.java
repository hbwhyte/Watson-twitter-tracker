package twitter.exceptions.custom_exceptions;

/**
 * Model for Custom Exception Message for the General Response
 */
public class CustomException {

    String errorName;
    String reason;

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
