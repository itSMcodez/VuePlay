package com.itsmcodez.themoviedbapi.model.tv.episode;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.IdElement;
import com.itsmcodez.themoviedbapi.model.core.image.Artwork;

@Data
@EqualsAndHashCode(callSuper = true)
public class Images extends IdElement {
    @JsonProperty("stills")
    private List<Artwork> stills = new ArrayList<>();
}
