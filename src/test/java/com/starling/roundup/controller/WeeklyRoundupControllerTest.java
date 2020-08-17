package com.starling.roundup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starling.roundup.model.WeeklyRoundupResponse;
import com.starling.roundup.service.WeeklyRoundupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeeklyRoundupController.class)
class WeeklyRoundupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WeeklyRoundupService weeklyRoundupService;

    @Test
    void whenServiceReturnsDataThenControllerReturnsTheSameData() throws Exception {

        WeeklyRoundupResponse expected = new WeeklyRoundupResponse(BigInteger.TEN, "fromId", "toId");

        when(weeklyRoundupService.doWeeklyRoundup("test")).thenReturn(expected);

        mockMvc.perform(get("/weekly-roundup").header("Access-Token", "test"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }
}