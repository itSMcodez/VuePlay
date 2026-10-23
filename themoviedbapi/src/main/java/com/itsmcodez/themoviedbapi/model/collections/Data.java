package com.itsmcodez.themoviedbapi.model.collections;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class Data extends AbstractJsonMapping {
    @JsonProperty("title")
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("homepage")
    private String homepage;
}
