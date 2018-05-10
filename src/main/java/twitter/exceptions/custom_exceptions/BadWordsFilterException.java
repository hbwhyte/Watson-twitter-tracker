package twitter.exceptions.custom_exceptions;

/**
 * Exception for errors caused by Bad Words Filter API
 *
 */
public class BadWordsFilterException extends Exception {

    public BadWordsFilterException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Bad Words Filter was not applied";
    }
}
