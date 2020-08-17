package com.starling.roundup.api.model;

import lombok.Data;

@Data
public class SufficientFundsResponse {

    private Boolean requestedAmountAvailableToSpend;
    private Boolean accountWouldBeInOverdraftIfRequestedAmountSpent;
}
