package com.starling.roundup.controller;

import com.starling.roundup.model.WeeklyRoundupResponse;
import com.starling.roundup.service.WeeklyRoundupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WeeklyRoundupController {

    private final WeeklyRoundupService weeklyRoundupService;

    @GetMapping(value = "/weekly-roundup")
    public ResponseEntity<WeeklyRoundupResponse> weeklyRoundup(@RequestHeader("Access-Token") String bearerToken) {

        return ResponseEntity.ok().body(weeklyRoundupService.doWeeklyRoundup(bearerToken));

    }
}
