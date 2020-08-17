package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starling.roundup.model.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class TransactionsFeedResponse {

    @JsonProperty("feedItems")
    private List<Transaction> transactions;

}
