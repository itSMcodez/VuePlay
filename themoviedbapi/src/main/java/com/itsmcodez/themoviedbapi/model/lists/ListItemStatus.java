package com.itsmcodez.themoviedbapi.model.lists;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.itsmcodez.themoviedbapi.model.core.IdElement;

/**
 * @author Holger Brandl
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListItemStatus extends IdElement {
    @JsonProperty("item_present")
    private Boolean itemPresent;
}
