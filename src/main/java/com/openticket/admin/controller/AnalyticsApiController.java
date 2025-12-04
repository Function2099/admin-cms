package com.openticket.admin.controller;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.openticket.admin.dto.AnalyticsDTO;
import com.openticket.admin.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsApiController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public AnalyticsDTO getAnalytics(
            @RequestParam List<Long> eventIds,
            @RequestParam(defaultValue = "merge") String mode,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return analyticsService.getAnalytics(eventIds, mode, startDate, endDate);
    }
}
