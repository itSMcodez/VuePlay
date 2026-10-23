package com.itsmcodez.themoviedbapi.model.find;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.tv.core.TvEpisode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FindTvEpisode extends TvEpisode {
    @JsonProperty("episode_type")
    private String episodeType;
}
