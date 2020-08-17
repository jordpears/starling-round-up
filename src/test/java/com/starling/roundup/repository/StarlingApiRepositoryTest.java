package com.starling.roundup.repository;

import com.starling.roundup.api.StarlingApi;
import com.starling.roundup.api.model.AllSavingsGoalsResponse;
import com.starling.roundup.api.model.CreateSavingsGoalResponse;
import com.starling.roundup.api.model.SufficientFundsResponse;
import com.starling.roundup.model.Account;
import com.starling.roundup.model.MonetaryAmount;
import com.starling.roundup.model.Transaction;
import com.starling.roundup.service.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StarlingApiRepositoryTest {

    StarlingApiRepository starlingApiRepository;

    @Mock
    CreateSavingsGoalResponse createSavingsGoalResponse;

    @Mock
    AllSavingsGoalsResponse allSavingsGoalsResponse;

    @Mock
    SufficientFundsResponse sufficientFundsResponse;

    @Mock
    StarlingApi starlingApi;

    @BeforeEach
    void before() {
        starlingApiRepository = new StarlingApiRepository(starlingApi);
    }

    @Test
    void whenNoSavingsGoalsForUserThenGetSavingsGoalIdCreatesNewOneAndReturnsNewId() {

        String expectedId = "targetUid";

        when(starlingApi.getAllUserSavingsGoals("accountId", "token")).thenReturn(allSavingsGoalsResponse);
        when(allSavingsGoalsResponse.getSavingsGoalList()).thenReturn(Collections.emptyList());
        when(starlingApi.putCreateSavingsGoal("accountId", "token")).thenReturn(createSavingsGoalResponse);
        when(createSavingsGoalResponse.getSavingsGoalUid()).thenReturn(expectedId);

        String actualId = starlingApiRepository.getSavingsGoalId("accountId", "token");

        verify(starlingApi).putCreateSavingsGoal("accountId", "token");
        assertEquals(expectedId, actualId);

    }

    @Test
    void whenCreateNewSavingsGoalCalledThenReturnsNewSavingsGoalId() {

        String expectedId = "expectedId";

        when(starlingApi.putCreateSavingsGoal("accountId", "token")).thenReturn(createSavingsGoalResponse);
        when(createSavingsGoalResponse.getSavingsGoalUid()).thenReturn("expectedId");

        String actualId = starlingApiRepository.createNewSavingsGoalInAccount("accountId", "token");

        assertEquals(expectedId, actualId);
    }

    @Test
    void whenIsSufficientFundsToRoundupCalledWithSufficientFundsThenReturnsTrue() {

        when(starlingApi.getSufficientFundsInAccount("accountId", BigInteger.TEN, "token")).thenReturn(sufficientFundsResponse);
        when(sufficientFundsResponse.getAccountWouldBeInOverdraftIfRequestedAmountSpent()).thenReturn(false);
        when(sufficientFundsResponse.getRequestedAmountAvailableToSpend()).thenReturn(true);

        assertTrue(starlingApiRepository.isSufficientFundsToRoundup("accountId", BigInteger.TEN, "token"));

    }

    @Test
    void whenIsSufficientFundsToRoundupCalledWithSufficientFundsButAccountWouldBeOverdrawnThenReturnsFalse() {

        when(starlingApi.getSufficientFundsInAccount("accountId", BigInteger.TEN, "token")).thenReturn(sufficientFundsResponse);
        when(sufficientFundsResponse.getAccountWouldBeInOverdraftIfRequestedAmountSpent()).thenReturn(true);
        when(sufficientFundsResponse.getRequestedAmountAvailableToSpend()).thenReturn(true);

        assertFalse(starlingApiRepository.isSufficientFundsToRoundup("accountId", BigInteger.TEN, "token"));

    }

    @Test
    void whenIsSufficientFundsToRoundupCalledWithoutSufficientFundsThenReturnsFalse() {

        when(starlingApi.getSufficientFundsInAccount("accountId", BigInteger.TEN, "token")).thenReturn(sufficientFundsResponse);
        when(sufficientFundsResponse.getRequestedAmountAvailableToSpend()).thenReturn(false);

        assertFalse(starlingApiRepository.isSufficientFundsToRoundup("accountId", BigInteger.TEN, "token"));

    }

    @Test
    void whenGetTransactionsSinceDateCalledThenTransactionsSinceDateReturned() {

        List<Transaction> expected = Collections.singletonList(new Transaction("defaultCategory", new MonetaryAmount("GBP", BigInteger.TEN), "OUT", "EXTERNAL"));

        when(starlingApi.getUserTransactions("accountId", "defaultCategory", OffsetDateTime.MAX, "token")).thenReturn(expected);

        List<Transaction> actual = starlingApiRepository.getTransactionsSinceDate("accountId", "defaultCategory", OffsetDateTime.MAX, "token");

        assertEquals(expected, actual);
    }

    @Test
    void whenGetMostRecentGBPUserAccountCalledWithTwoAccountsThenMostRecentReturned() {

        Account oldAccount = new Account("id1", "testCategory", "GBP", OffsetDateTime.now().minusDays(10));
        Account newAccount = new Account("id2", "testCategory", "GBP", OffsetDateTime.now());

        when(starlingApi.getUserAccounts("token")).thenReturn(Arrays.asList(oldAccount, newAccount));

        Account actual = starlingApiRepository.getMostRecentGBPUserAccount("token");

        assertEquals(newAccount, actual);

    }

    @Test
    void whenGetMostRecentGBPUserAccountCalledWithTwoAccountsOlderGBPOnlyThenOlderGBPOneReturned() {

        Account oldGBPAccount = new Account("id1", "testCategory", "GBP", OffsetDateTime.now().minusDays(10));
        Account newEURAccount = new Account("id2", "testCategory", "EUR", OffsetDateTime.now());

        when(starlingApi.getUserAccounts("token")).thenReturn(Arrays.asList(oldGBPAccount, newEURAccount));

        Account actual = starlingApiRepository.getMostRecentGBPUserAccount("token");

        assertEquals(oldGBPAccount, actual);

    }

    @Test
    void whenGetMostRecentGBPUserAccountCalledNoAccountsThenBussinessLogicExceptionThrown() {

        when(starlingApi.getUserAccounts("token")).thenReturn(Collections.emptyList());

        assertThrows(BusinessLogicException.class, () -> starlingApiRepository.getMostRecentGBPUserAccount("token"));

    }

    @Test
    void whenTransferFundsToSavingsPotCalledWhenSufficientFundsThenMoneyTransferred() {

        when(starlingApi.getSufficientFundsInAccount("accountId", BigInteger.TEN, "token")).thenReturn(sufficientFundsResponse);
        when(sufficientFundsResponse.getAccountWouldBeInOverdraftIfRequestedAmountSpent()).thenReturn(false);
        when(sufficientFundsResponse.getRequestedAmountAvailableToSpend()).thenReturn(true);

        starlingApiRepository.transferFundsToSavingsPot("accountId", BigInteger.TEN, "goalId", "token");

        verify(starlingApi).putMoneyToSavingsGoalWithAmount("accountId", "goalId", BigInteger.TEN, "token");

    }

    @Test
    void whenTransferFundsToSavingsPotCalledWhenNotSufficientFundsThenBusinessLogicExceptionThrown() {

        when(starlingApi.getSufficientFundsInAccount("accountId", BigInteger.TEN, "token")).thenReturn(sufficientFundsResponse);
        when(sufficientFundsResponse.getRequestedAmountAvailableToSpend()).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> starlingApiRepository.transferFundsToSavingsPot("accountId", BigInteger.TEN, "goalId", "token"));

    }
}