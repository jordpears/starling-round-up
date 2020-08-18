package com.starling.roundup.service;

import com.starling.roundup.model.Account;
import com.starling.roundup.model.MonetaryAmount;
import com.starling.roundup.model.Transaction;
import com.starling.roundup.model.WeeklyRoundupResponse;
import com.starling.roundup.repository.StarlingApiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyRoundupServiceTest {

    @Mock
    private StarlingApiRepository starlingApiRepository;

    @Mock
    private Account account;

    @Mock
    private Transaction transaction;

    private WeeklyRoundupService weeklyRoundupService;

    private Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());

    @BeforeEach
    void setup() {

        weeklyRoundupService = new WeeklyRoundupService(clock, starlingApiRepository);
    }

    @Test
    void whenDoWeeklyRoundupCalledAndAPIsReturnOKThenCorrectModelReturned() {

        String expectedAccountId = "testId";
        MonetaryAmount expectedRoundupAmount = new MonetaryAmount("GBP", BigInteger.valueOf(95));
        String expectedSavingsGoalId = "savingsGoalIdd";

        WeeklyRoundupResponse expectedResponse = new WeeklyRoundupResponse(expectedRoundupAmount, expectedAccountId, expectedSavingsGoalId);

        when(transaction.getDirection()).thenReturn("OUT");
        when(transaction.getSource()).thenReturn("EXTERNAL");
        when(transaction.getBalance()).thenReturn(new MonetaryAmount("GBP", BigInteger.valueOf(105)));

        when(starlingApiRepository.getMostRecentGBPUserAccount("token")).thenReturn(account);

        when(account.getAccountId()).thenReturn("testId");
        when(account.getDefaultCategory()).thenReturn("testCategoryId");

        when(starlingApiRepository.getTransactionsSinceDate(expectedAccountId, "testCategoryId", OffsetDateTime.now(clock).minusDays(7), "token")).thenReturn(Collections.singletonList(transaction));
        when(starlingApiRepository.getSavingsGoalId("testId", "token")).thenReturn(expectedSavingsGoalId);

        assertEquals(expectedResponse, weeklyRoundupService.doWeeklyRoundup("token"));
    }

    @Test
    void whenDoWeeklyRoundupCalledAndNoTransactionsInPeriodThenCorrectModelReturned() {

        WeeklyRoundupResponse expectedResponse = new WeeklyRoundupResponse(new MonetaryAmount("GBP", BigInteger.ZERO), "", "");

        when(starlingApiRepository.getMostRecentGBPUserAccount("token")).thenReturn(account);

        when(account.getAccountId()).thenReturn("testId");
        when(account.getDefaultCategory()).thenReturn("testCategoryId");

        when(starlingApiRepository.getTransactionsSinceDate("testId", "testCategoryId", OffsetDateTime.now(clock).minusDays(7), "token")).thenReturn(Collections.emptyList());

        verifyNoMoreInteractions(starlingApiRepository);
        assertEquals(expectedResponse, weeklyRoundupService.doWeeklyRoundup("token"));
    }

    @Test
    void whenNoTransactionsInPeriodThenGetRoundupAmountReturns0() {

        assertEquals(BigInteger.ZERO, weeklyRoundupService.getRoundupAmount(Collections.emptyList()));

    }

    @Test
    void whenTransactionOfValue105InPeriodThenGetRoundupAmountReturns95() {

        Transaction transaction = new Transaction("testUid", new MonetaryAmount("GBP", BigInteger.valueOf(105)), "OUT", "EXTERNAL");

        assertEquals(BigInteger.valueOf(95), weeklyRoundupService.getRoundupAmount(Collections.singletonList(transaction)));

    }

    @Test
    void whenTransactionOfValue100InPeriodThenGetRoundupAmountReturns0() {

        Transaction transaction = new Transaction("testUid", new MonetaryAmount("GBP", BigInteger.valueOf(100)), "OUT", "EXTERNAL");

        assertEquals(BigInteger.ZERO, weeklyRoundupService.getRoundupAmount(Collections.singletonList(transaction)));

    }

    @Test
    void whenTwoTransactionsOfValue50InPeriodThenGetRoundupAmountReturns100() {

        Transaction transaction = new Transaction("testUid", new MonetaryAmount("GBP", BigInteger.valueOf(50)), "OUT", "EXTERNAL");

        assertEquals(BigInteger.valueOf(100), weeklyRoundupService.getRoundupAmount(Arrays.asList(transaction, transaction)));

    }

    @Test
    void whenTransactionNotOutGoingAndValue50InPeriodThenGetRoundupAmountDoesntIncludeTransaction() {

        Transaction transaction = new Transaction("testUid", new MonetaryAmount("GBP", BigInteger.valueOf(100)), "IN", "EXTERNAL");

        assertEquals(BigInteger.ZERO, weeklyRoundupService.getRoundupAmount(Collections.singletonList(transaction)));

    }

    @Test
    void whenTransactionSourceInternalTransferAndValue50InPeriodThenGetRoundupAmountDoesntIncludeTransaction() {

        Transaction transaction = new Transaction("testUid", new MonetaryAmount("GBP", BigInteger.valueOf(50)), "OUT", "INTERNAL_TRANSFER");

        assertEquals(BigInteger.ZERO, weeklyRoundupService.getRoundupAmount(Collections.singletonList(transaction)));

    }

}