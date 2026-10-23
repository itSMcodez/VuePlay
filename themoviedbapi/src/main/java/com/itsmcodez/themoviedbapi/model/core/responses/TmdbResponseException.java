package com.itsmcodez.themoviedbapi.model.core.responses;

import lombok.Getter;
import lombok.ToString;
import com.itsmcodez.themoviedbapi.tools.TmdbException;
import com.itsmcodez.themoviedbapi.tools.TmdbResponseCode;

@Getter
@ToString
public class TmdbResponseException extends TmdbException {
    private final TmdbResponseCode responseCode;

    public TmdbResponseException(TmdbResponseCode responseCode) {
        super(responseCode.toString());
        this.responseCode = responseCode;
    }

    public TmdbResponseException(String message) {
        super(message);
        this.responseCode = null;
    }

    public TmdbResponseException(Exception exception) {
        super(exception);
        this.responseCode = null;
    }
}
