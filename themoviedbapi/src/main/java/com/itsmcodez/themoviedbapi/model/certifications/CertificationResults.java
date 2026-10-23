package com.itsmcodez.themoviedbapi.model.certifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.AbstractJsonMapping;

@Data
@EqualsAndHashCode(callSuper = false)
public class CertificationResults extends AbstractJsonMapping {
    @JsonProperty("certifications")
    private Map<String, List<Certification>> certifications = new HashMap<>();
}
