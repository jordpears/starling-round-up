package com.starling.roundup.api.model;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddGBPMoneySavingsGoalRequestTest {

    @Test
    void whenWithCalledThenCorrectObjectCreated() {

        BigInteger expectedValue = BigInteger.TEN;
        AddGBPMoneySavingsGoalRequest actual = AddGBPMoneySavingsGoalRequest.withAmount(expectedValue);

        assertEquals(expectedValue, actual.getAmount().getMinorUnits());

    }
}