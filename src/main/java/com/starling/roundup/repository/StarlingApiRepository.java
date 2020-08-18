package com.starling.roundup.repository;

import com.starling.roundup.api.StarlingApi;
import com.starling.roundup.api.model.AllSavingsGoalsResponse;
import com.starling.roundup.api.model.CreateSavingsGoalResponse;
import com.starling.roundup.api.model.SufficientFundsResponse;
import com.starling.roundup.model.Account;
import com.starling.roundup.model.Transaction;
import com.starling.roundup.service.exception.BusinessLogicException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@AllArgsConstructor
public class StarlingApiRepository {

    private final StarlingApi starlingApi;

    public String getSavingsGoalId(String accountId, String bearerToken) {

        AllSavingsGoalsResponse allSavingsGoalsResponse = starlingApi.getAllUserSavingsGoals(accountId, bearerToken);

        if (allSavingsGoalsResponse.getSavingsGoalList() == null || allSavingsGoalsResponse.getSavingsGoalList().isEmpty()) {
            return createNewSavingsGoalInAccount(accountId, bearerToken);
        } else {
            return allSavingsGoalsResponse.getSavingsGoalList().get(0).getSavingsGoalId();
        }

    }

    public String createNewSavingsGoalInAccount(String accountId, String bearerToken) {

        CreateSavingsGoalResponse createSavingsGoalResponse = starlingApi.putCreateSavingsGoal(accountId, bearerToken);

        return createSavingsGoalResponse.getSavingsGoalUid();
    }

    public boolean isSufficientFundsToRoundup(String accountId, BigInteger amount, String bearerToken) {

        SufficientFundsResponse sufficientFundsResponse = starlingApi.getSufficientFundsInAccount(accountId, amount, bearerToken);

        return sufficientFundsResponse.getRequestedAmountAvailableToSpend() && !sufficientFundsResponse.getAccountWouldBeInOverdraftIfRequestedAmountSpent();

    }

    public List<Transaction> getTransactionsSinceDate(String accountId, String defaultCategory, OffsetDateTime sinceDate, String bearerToken) {

        return starlingApi.getUserTransactions(accountId, defaultCategory, sinceDate, bearerToken);

    }

    public Account getMostRecentGBPUserAccount(String bearerToken) {

        return starlingApi.getUserAccounts(bearerToken).stream()
                .sorted(Comparator.comparing(Account::getCreatedAt).reversed())
                .filter(account -> account.getCurrency().equals("GBP"))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException("No GBP accounts found for user"));
    }


    public void transferFundsToSavingsPot(String accountId, BigInteger roundupAmount, String savingsGoalId, String bearerToken) {

        if (isSufficientFundsToRoundup(accountId, roundupAmount, bearerToken)) {
            starlingApi.putMoneyToSavingsGoalWithAmount(accountId, savingsGoalId, roundupAmount, bearerToken);
        } else {
            throw new BusinessLogicException("Account has insufficient funds to complete weekly roundup.");
        }
    }
}
