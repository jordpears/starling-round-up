package com.starling.roundup.api.model;

import com.starling.roundup.model.MonetaryAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSavingsGoalRequest {

    private String name;
    private String currency;
    private MonetaryAmount target;
    private String base64EncodedPhoto;

    public static CreateSavingsGoalRequest withDefaultValues() {

        String name = "My savings goal!";
        String currency = "GBP";
        MonetaryAmount target = new MonetaryAmount("GBP", BigInteger.valueOf(100000));
        String base64EncodedPhoto = null;

        return new CreateSavingsGoalRequest(name, currency, target, base64EncodedPhoto);
    }
}
