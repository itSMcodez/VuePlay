package com.itsmcodez.themoviedbapi.model.rated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.Movie;

@Data
@EqualsAndHashCode(callSuper = true)
public class RatedMovie extends Movie {
    @JsonProperty("rating")
    private Double rating;
}
