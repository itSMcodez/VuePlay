package com.itsmcodez.themoviedbapi.model.credits;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.NamedIdElement;
import com.itsmcodez.themoviedbapi.model.people.Gender;

@Data
@EqualsAndHashCode(callSuper = true)
public class Person extends NamedIdElement {
    @JsonProperty("adult")
    private Boolean adult;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("known_for_department")
    private String knownForDepartment;

    @JsonProperty("media_type")
    private MediaType mediaType;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("profile_path")
    private String profilePath;
}
