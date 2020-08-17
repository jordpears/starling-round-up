package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllSavingsGoalsResponse {

    @JsonProperty("savingsGoalList")
    private List<SavingsGoalResponse> savingsGoalList;
}
