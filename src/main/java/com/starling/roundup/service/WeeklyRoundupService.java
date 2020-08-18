package com.starling.roundup.service;

import com.starling.roundup.model.Account;
import com.starling.roundup.model.MonetaryAmount;
import com.starling.roundup.model.Transaction;
import com.starling.roundup.model.WeeklyRoundupResponse;
import com.starling.roundup.repository.StarlingApiRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class WeeklyRoundupService {

    private final Clock clock; //used for testing
    private final StarlingApiRepository starlingApiRepository;

    public WeeklyRoundupResponse doWeeklyRoundup(String bearerToken) {

        OffsetDateTime dateOneWeekAgo = OffsetDateTime.now(clock).minusDays(7);

        Account account = starlingApiRepository.getMostRecentGBPUserAccount(bearerToken);
        List<Transaction> transactions = starlingApiRepository.getTransactionsSinceDate(account.getAccountId(), account.getDefaultCategory(), dateOneWeekAgo, bearerToken);
        BigInteger roundupAmount = getRoundupAmount(transactions);

        if(roundupAmount.equals(BigInteger.ZERO)){
            return new WeeklyRoundupResponse(new MonetaryAmount("GBP", roundupAmount), "", "");
        } else {
            String savingsGoalId = starlingApiRepository.getSavingsGoalId(account.getAccountId(), bearerToken);
            starlingApiRepository.transferFundsToSavingsPot(account.getAccountId(), roundupAmount, savingsGoalId, bearerToken);
            return new WeeklyRoundupResponse(new MonetaryAmount("GBP", roundupAmount), account.getAccountId(), savingsGoalId);
        }

    }

    public BigInteger getRoundupAmount(List<Transaction> transactions) {

        return transactions.stream()
                .filter(transaction -> transaction.getDirection().equals("OUT"))
                .filter(transaction -> !transaction.getSource().equals("INTERNAL_TRANSFER"))
                .map(Transaction::getBalance)
                .map(MonetaryAmount::getMinorUnits)
                .filter(minorUnit -> !minorUnit.remainder(BigInteger.valueOf(100)).equals(BigInteger.ZERO)) //choose not to round up from 0.
                .map(minorUnit -> BigInteger.valueOf(100).subtract(minorUnit.remainder(BigInteger.valueOf(100))))
                .reduce(BigInteger.ZERO, BigInteger::add);

    }
}
