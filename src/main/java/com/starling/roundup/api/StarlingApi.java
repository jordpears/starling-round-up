package com.starling.roundup.api;


import com.starling.roundup.api.model.*;
import com.starling.roundup.model.Account;
import com.starling.roundup.model.Transaction;
import com.starling.roundup.service.exception.BusinessLogicException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class StarlingApi {

    private HttpHeaders headers;
    private RestTemplate restTemplate;

    private final String BASE_URL = "https://api-sandbox.starlingbank.com/";

    private final String ACCOUNTS_ENDPOINT = "api/v2/accounts";
    private final String TRANSACTIONS_ENDPOINT = "api/v2/feed/account/{accountUid}/category/{categoryUid}";
    private final String CREATE_SAVINGS_GOAL_ENDPOINT = "/api/v2/account/{accountUid}/savings-goals";
    private final String ADD_MONEY_SAVINGS_GOAL_ENDPOINT = "/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}";
    private final String SUFFICIENT_FUNDS_ENDPOINT = "/api/v2/accounts/{accountUid}/confirmation-of-funds";
    private final String ALL_SAVINGS_GOALS_ENDPOINT = "/api/v2/account/{accountUid}/savings-goals";

    public SufficientFundsResponse getSufficientFundsInAccount(String accountId, BigInteger amount, String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(SUFFICIENT_FUNDS_ENDPOINT)
                .queryParam("targetAmountInMinorUnits", amount)
                .buildAndExpand(accountId);

        ResponseEntity<SufficientFundsResponse> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, SufficientFundsResponse.class);

        return response.getBody();

    }

    public void putMoneyToSavingsGoalWithAmount(String accountId, String savingsGoalId, BigInteger amount, String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity<AddGBPMoneySavingsGoalRequest> entity = new HttpEntity<>(AddGBPMoneySavingsGoalRequest.withAmount(amount), headers);

        String transferId = UUID.randomUUID().toString();

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(ADD_MONEY_SAVINGS_GOAL_ENDPOINT)
                .buildAndExpand(accountId, savingsGoalId, transferId);

        restTemplate.exchange(uriComponents.toString(), HttpMethod.PUT, entity, AddMoneySavingsGoalResponse.class);

    }

    public CreateSavingsGoalResponse putCreateSavingsGoal(String accountId, String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity<CreateSavingsGoalRequest> entity = new HttpEntity<>(CreateSavingsGoalRequest.withDefaultValues(), headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(CREATE_SAVINGS_GOAL_ENDPOINT)
                .buildAndExpand(accountId);

        ResponseEntity<CreateSavingsGoalResponse> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.PUT, entity, CreateSavingsGoalResponse.class);

        return response.getBody();
    }

    public List<Account> getUserAccounts(String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(ACCOUNTS_ENDPOINT)
                .build();

        ResponseEntity<UserAccountsResponse> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, UserAccountsResponse.class);

        if (response.getBody() == null || response.getBody().getAccounts() == null) {
            throw new BusinessLogicException("No accounts found for user");
        }

        return response.getBody().getAccounts();
    }

    public List<Transaction> getUserTransactions(String accountUid, String defaultCategory, OffsetDateTime startDate, String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(TRANSACTIONS_ENDPOINT)
                .queryParam("changesSince", startDate.format(DateTimeFormatter.ISO_INSTANT))
                .buildAndExpand(accountUid, defaultCategory);

        ResponseEntity<TransactionsFeedResponse> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, TransactionsFeedResponse.class);

        if (response.getBody() == null) {
            return Collections.emptyList();
        }

        return response.getBody().getTransactions();

    }

    public AllSavingsGoalsResponse getAllUserSavingsGoals(String accountId, String bearerToken) {

        headers.setBearerAuth(bearerToken);
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(ALL_SAVINGS_GOALS_ENDPOINT)
                .buildAndExpand(accountId);

        ResponseEntity<AllSavingsGoalsResponse> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, AllSavingsGoalsResponse.class);

        return response.getBody();

    }

}
