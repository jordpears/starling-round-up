package com.starling.roundup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @JsonProperty("accountUid")
    private String accountId;
    @JsonProperty("defaultCategory")
    private String defaultCategory;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

}
