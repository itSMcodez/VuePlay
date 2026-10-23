package com.itsmcodez.themoviedbapi.model.tv.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class Data extends AbstractJsonMapping {
    @JsonProperty("name")
    private String name;

    @JsonProperty("overview")
    private String overview;
}
