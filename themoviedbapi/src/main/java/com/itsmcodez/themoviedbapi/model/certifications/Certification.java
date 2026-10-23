package com.itsmcodez.themoviedbapi.model.certifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@Data
@EqualsAndHashCode(callSuper = false)
public class Certification extends AbstractJsonMapping {
    @JsonProperty("certification")
    private String certification;

    @JsonProperty("meaning")
    private String meaning;

    @JsonProperty("order")
    private Integer order;
}
