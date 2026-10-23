package com.itsmcodez.themoviedbapi.model.changes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.IdElement;

@Data
@EqualsAndHashCode(callSuper = true)
public class Change extends IdElement {
    @JsonProperty("adult")
    private Boolean adult;
}
