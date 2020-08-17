package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddMoneySavingsGoalResponse {

    @JsonProperty("transferUid")
    private String transferId;
    @JsonProperty("success")
    private Boolean success;

}
