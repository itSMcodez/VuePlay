package com.itsmcodez.themoviedbapi.model.tv.episodegroups;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.NamedStringIdElement;

@Data
@EqualsAndHashCode(callSuper = true)
public class TvEpisodeGroup extends NamedStringIdElement {
    @JsonProperty("order")
    private Integer order;

    @JsonProperty("episodes")
    private List<OrderedTvEpisode> episodes = new ArrayList<>();

    @JsonProperty("locked")
    private Boolean locked;
}
