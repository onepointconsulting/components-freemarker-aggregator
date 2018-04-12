package org.talend.components.onepoint.freemarker.runtime.writer;

/**
 * Contains the error issued when the processing by Freemarker cannot be completed.
 */
class FreemarkerError {

    private final boolean success;

    private String errorMessage;

    private Exception exception;

    FreemarkerError(boolean success) {
        this.success = success;
    }

    FreemarkerError(boolean success, String errorMessage, Exception exception) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    boolean isSuccess() {
        return success;
    }

    String getErrorMessage() {
        return errorMessage;
    }

    public Exception getException() {
        return exception;
    }
}
