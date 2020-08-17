package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class CreateSavingsGoalResponse {

    private String savingsGoalUid;
    private Boolean success;
}
