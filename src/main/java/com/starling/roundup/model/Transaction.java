package com.starling.roundup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @JsonProperty("categoryUid")
    private String categoryUid;
    @JsonProperty("sourceAmount")
    private MonetaryAmount balance;
    @JsonProperty("direction")
    private String direction;
    @JsonProperty("source")
    private String source;

}
