package twitter.model;

import org.springframework.http.HttpStatus;

/**
 * General response object that should be returned by the API regardless
 * of a successful or failed call
 */
public class GeneralResponse {

    String status = "Success";
    HttpStatus response_code = HttpStatus.OK;
    Object data;
    Object error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HttpStatus getResponse_code() {
        return response_code;
    }

    public void setResponse_code(HttpStatus response_code) {
        this.response_code = response_code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}

