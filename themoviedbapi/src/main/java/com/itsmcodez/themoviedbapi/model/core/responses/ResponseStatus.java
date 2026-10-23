package com.itsmcodez.themoviedbapi.model.core.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;
import com.itsmcodez.themoviedbapi.tools.TmdbResponseCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseStatus extends AbstractJsonMapping {
    @JsonProperty("status_code")
    private TmdbResponseCode statusCode;

    @JsonProperty("status_message")
    private String statusMessage;
}
