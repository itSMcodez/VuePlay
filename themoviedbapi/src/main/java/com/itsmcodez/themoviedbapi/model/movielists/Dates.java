package com.itsmcodez.themoviedbapi.model.movielists;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@Data
@EqualsAndHashCode(callSuper = false)
public class Dates extends AbstractJsonMapping {
    @JsonProperty("maximum")
    private String maximum;

    @JsonProperty("minimum")
    private String minimum;
}
