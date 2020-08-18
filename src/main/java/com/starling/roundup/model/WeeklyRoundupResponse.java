package com.starling.roundup.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyRoundupResponse {

    private MonetaryAmount roundupAmount;
    private String fromAccountId;
    private String toSavingsGoalId;
}
