package com.itsmcodez.themoviedbapi.model.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@lombok.Data
@EqualsAndHashCode(callSuper = false)
public class Data extends AbstractJsonMapping {
    @JsonProperty("biography")
    private String biography;
}
