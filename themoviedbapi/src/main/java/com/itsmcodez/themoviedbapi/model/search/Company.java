package com.itsmcodez.themoviedbapi.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.NamedIdElement;

@Data
@EqualsAndHashCode(callSuper = true)
public class Company extends NamedIdElement {
    @JsonProperty("logo_path")
    private String logoPath;

    @JsonProperty("origin_country")
    private String originCountry;
}
