package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starling.roundup.model.MonetaryAmount;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AddGBPMoneySavingsGoalRequest {

    @JsonProperty("amount")
    private MonetaryAmount amount;

    public static AddGBPMoneySavingsGoalRequest withAmount(BigInteger amount) {

        AddGBPMoneySavingsGoalRequest addGBPMoneySavingsGoalRequest = new AddGBPMoneySavingsGoalRequest();
        addGBPMoneySavingsGoalRequest.setAmount(new MonetaryAmount("GBP", amount));

        return addGBPMoneySavingsGoalRequest;

    }
}
