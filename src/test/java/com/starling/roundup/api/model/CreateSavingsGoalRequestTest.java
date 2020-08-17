package com.starling.roundup.api.model;

import com.starling.roundup.model.MonetaryAmount;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateSavingsGoalRequestTest {

    @Test
    void whenDefaultRequestCalledThenCorrectObjectCreated() {

        CreateSavingsGoalRequest request = CreateSavingsGoalRequest.withDefaultValues();

        assertEquals("My savings goal!", request.getName());
        assertEquals(new MonetaryAmount("GBP", BigInteger.valueOf(100000)), request.getTarget());
        assertEquals("GBP", request.getCurrency());
        assertNull(request.getBase64EncodedPhoto());

    }
}