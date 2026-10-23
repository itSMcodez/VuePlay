package com.itsmcodez.themoviedbapi.model.core.multi;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.TvSeries;

@Data
@EqualsAndHashCode(callSuper = true)
public class MultiTvSeries extends TvSeries implements Multi {
    @Override
    public MediaType getMediaType() {
        return MediaType.TV_SERIES;
    }
}
