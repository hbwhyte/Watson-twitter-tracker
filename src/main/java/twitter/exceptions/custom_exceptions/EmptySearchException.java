package twitter.exceptions.custom_exceptions;

/**
 * Exception when the Twitter call was successful, but there
 * just are not any tweets about a particular subject
 */
public class EmptySearchException extends Exception {

    public EmptySearchException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Empty Search";
    }
}
