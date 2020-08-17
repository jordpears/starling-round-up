package com.starling.roundup.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starling.roundup.model.Account;
import lombok.Data;

import java.util.List;

@Data
public class UserAccountsResponse {

    @JsonProperty("accounts")
    private List<Account> accounts;

}
