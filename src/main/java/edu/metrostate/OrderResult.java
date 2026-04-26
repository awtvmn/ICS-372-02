package edu.metrostate;

/**
 * OrderResult class - stores the result of an OrderManager action.
 * Contains a message describing what happened.
 */
public class OrderResult {
    private final boolean success;
    private final String  message;

    /**
     * Constructs an OrderResult
     * @param success true if the operation succeeded, false otherwise
     * @param message a description of the outcome
     */
    public OrderResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Returns the message describing the outcome.
     * @return message string
     */
    public String getMessage() { return message; }
}
