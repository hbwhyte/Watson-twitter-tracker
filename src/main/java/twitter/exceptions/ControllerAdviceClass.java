package twitter.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import twitter.model.GeneralResponse;
import twitter4j.TwitterException;

import java.io.UnsupportedEncodingException;


@ControllerAdvice
public class ControllerAdviceClass {

    /**
     * If the user tries a path or page that does not exist,
     * returns this exception message.
     *
     * @param e NoHandlerFoundException
     * @return GeneralResponse with a failed 404 message
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoHandlerFoundException.class})
    public @ResponseBody
    GeneralResponse handle404(NoHandlerFoundException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 404: NOT FOUND
        gr.setResponse_code(HttpStatus.NOT_FOUND);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Page Not Found");
        error.setReason("Invalid " + e.getHttpMethod() + " request at path " + e.getRequestURL() +
                " - Please check your URL and try again.");
        gr.setError(error);
        return gr;
    }

    /**
     * If the user is missing or has errors in any of their
     * Twitter credentials.
     *
     * @param e IllegalStateException
     * @return GeneralResponse with a failed 500 message
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IllegalStateException.class})
    public @ResponseBody
    GeneralResponse badCredentials(IllegalStateException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 500: INTERNAL SERVER ERROR
        gr.setResponse_code(HttpStatus.INTERNAL_SERVER_ERROR);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Illegal State Exception: Bad Credentials");
        error.setReason(e.getMessage());

        gr.setError(error);
        return gr;
    }

    /**
     * If the user tries an incompatible, unmapped, or not allowed
     * HTTP request, returns this exception message. For example,
     * trying to POST to a path that only accepts GET requests.
     *
     * @param e HttpRequestMethodNotSupportedException
     * @return GeneralResponse with a failed 405 message
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public @ResponseBody
    GeneralResponse handle405(HttpRequestMethodNotSupportedException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 405 Method Not Allowed
        gr.setResponse_code(HttpStatus.METHOD_NOT_ALLOWED);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Page Not Found");
        error.setReason(e.getMessage());

        gr.setError(error);
        return gr;
    }

    /**
     * If the user does not include required request parameters in their
     * HTTP request, returns this exception message.
     *
     * @param e MissingServletRequestParameterException
     * @return GeneralResponse with a failed 400 message
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    GeneralResponse handle400(MissingServletRequestParameterException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 400 BAD REQUEST
        gr.setResponse_code(HttpStatus.BAD_REQUEST);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Missing Request Parameters");
        error.setReason(e.getMessage());
        gr.setError(error);
        return gr;
    }

    /**
     * If the user is missing the required RequestBody or
     * includes unreadable JSON to in their HTTP
     * request, returns this exception message. For example, one
     * sided quotes in their POST RequestBody.
     *
     * @param e HttpMessageNotReadableException
     * @return GeneralResponse with a failed 400 message
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    GeneralResponse handle400(HttpMessageNotReadableException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 400 BAD REQUEST
        gr.setResponse_code(HttpStatus.BAD_REQUEST);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("HTTP Request Message Error");
        error.setReason(e.getMessage());
        gr.setError(error);
        return gr;
    }

    /**
     * Handles any rejected requests from Twitter thrown
     * by Twitter4j's TwitterException
     *
     * @param e TwitterException
     * @return GeneralResponse with a failed 403 message
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({TwitterException.class})
    public @ResponseBody
    GeneralResponse twitterError(TwitterException e) {
        GeneralResponse gr = new GeneralResponse();
        // Change GeneralResponse status to Fail
        gr.setStatus("Fail");
        // HTTP Status 400 FORBIDDEN
        gr.setResponse_code(HttpStatus.FORBIDDEN);

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Twitter Exception");
        error.setReason(e.getMessage());

        gr.setError(error);
        return gr;
    }

    /**
     * If the hashtags were unable to encoded
     *
     * @param e UnsupportedEncodingException
     * @return GeneralResponse with a success 200 message, side note error
     */
    @ExceptionHandler({UnsupportedEncodingException.class})
    public @ResponseBody
    GeneralResponse twitterError(UnsupportedEncodingException e) {
        GeneralResponse gr = new GeneralResponse();

        // Create error JSON response
        CustomException error = new CustomException();
        error.setErrorName("Hashtags were not encoded. Not fatal error.");
        error.setReason(e.getMessage());

        gr.setError(error);
        return gr;
    }

    /**
     * Generates a nicely formatted Custom Exception response
     * for JSON output.
     *
     * @param e Any exception to be formatted
     * @return CustomException Object
     */
    public CustomException generateCustomEx(Exception e) {
        CustomException c = new CustomException();
        c.setErrorName(e.toString());
        c.setReason(e.getMessage());
        return c;
    }
}
