package com.starling.roundup.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

@Data
@AllArgsConstructor
public class WeeklyRoundupResponse {

    private BigInteger roundupAmountMinorUnit;
    private String fromAccountId;
    private String toSavingsGoalId;
}
