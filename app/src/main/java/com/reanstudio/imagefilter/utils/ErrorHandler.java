package com.reanstudio.imagefilter.utils;

/**
 * Created by yahyamukhlis on 4/28/16.
 */
public interface ErrorHandler {
    enum ErrorType {
        BUFFER_CREATION_ERROR
    }

    void handleError(ErrorType errorType, String cause);
}
