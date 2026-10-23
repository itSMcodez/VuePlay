package com.itsmcodez.themoviedbapi.model.reviews;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.NamedElement;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorDetails extends NamedElement {
    @JsonProperty("username")
    private String username;

    @JsonProperty("avatar_path")
    private String avatarPath;

    @JsonProperty("rating")
    private String rating;
}
