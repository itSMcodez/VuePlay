package com.itsmcodez.themoviedbapi.model.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.keywords.Keyword;

@Data
@EqualsAndHashCode(callSuper = true)
public class TvKeywords extends Results<Keyword> {
}
