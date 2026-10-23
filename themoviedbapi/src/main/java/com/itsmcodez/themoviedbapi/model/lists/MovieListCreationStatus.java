package com.itsmcodez.themoviedbapi.model.lists;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.responses.ResponseStatusAuthentication;

@Data
@EqualsAndHashCode(callSuper = true)
public class MovieListCreationStatus extends ResponseStatusAuthentication {
    @JsonProperty("list_id")
    private String listId;
}
