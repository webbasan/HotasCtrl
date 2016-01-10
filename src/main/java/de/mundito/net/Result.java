package de.mundito.net;

/**
 * User: webbasan Date: 10.01.16 Time: 16:39
 */
public enum Result {
    OK(200, "OK", "Done."),
    UNKNOWN_COMMAND(404, "Not Found", "Unknown command resource."),
    INVALID_ARGUMENTS(400, "Bad Request", "Invalid arguments."),
    INTERNAL_ERROR(500, "Internal Server Error", "Unexpected internal error in application."),
    UNAVAILABLE(503, "Service Unavailable", "HOTAS currently offline.");

    public final int responseCode;
    public final String responseMessage;
    public final String longMessage;

    Result(final int responseCode, final String responseMessage, final String longMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.longMessage = longMessage;
    }
}
