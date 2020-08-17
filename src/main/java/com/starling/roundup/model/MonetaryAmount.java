package com.starling.roundup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonetaryAmount {

    @JsonProperty("currency")
    private String currency;
    @JsonProperty("minorUnits")
    private BigInteger minorUnits;
}