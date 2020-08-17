package com.starling.roundup.controller;

import com.starling.roundup.service.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TopLevelExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private WeeklyRoundupController weeklyRoundupController;

    @BeforeEach
    void setup() {

        this.mockMvc = MockMvcBuilders.standaloneSetup(weeklyRoundupController)
                .setControllerAdvice(new TopLevelExceptionHandler())
                .build();
    }

    @Test
    void whenRuntimeExceptionThrownThenServiceReturns500WithNoText() throws Exception {

        when(weeklyRoundupController.weeklyRoundup("test")).thenThrow(new RuntimeException("test exception"));

        mockMvc.perform(get("/weekly-roundup").header("Access-Token", "test"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));
    }

    @Test
    void whenBusinessLogicExceptionThrownThenServiceReturnsBadRequestAndErrorMessage() throws Exception {

        String expectedMessage = "expected text";

        when(weeklyRoundupController.weeklyRoundup("test")).thenThrow(new BusinessLogicException(expectedMessage));

        mockMvc.perform(get("/weekly-roundup").header("Access-Token", "test"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void whenHttpClientErrorExceptionThrownWith403ThenServiceReturns403AndCorrectMessage() throws Exception { //thrown by RestTemplate http client

        String expectedMessage = "Forbidden - validate 'Access-Token' header";

        when(weeklyRoundupController.weeklyRoundup("test")).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "FORBIDDEN"));

        mockMvc.perform(get("/weekly-roundup").header("Access-Token", "test"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(expectedMessage));
    }

    @Test
    void whenHttpServerErrorExceptionThrownThenServiceReturnsServiceUnavailableAndNoMessage() throws Exception { //thrown by RestTemplate http client

        when(weeklyRoundupController.weeklyRoundup("test")).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "error text"));

        mockMvc.perform(get("/weekly-roundup").header("Access-Token", "test"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(""));
    }
}