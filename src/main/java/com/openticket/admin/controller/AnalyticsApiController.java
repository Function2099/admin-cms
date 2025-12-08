package com.openticket.admin.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openticket.admin.dto.AnalyticsDTO;
import com.openticket.admin.service.AnalyticsService;

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

    @GetMapping("/overview")
    public AnalyticsDTO.Overview getTotalOverview(
            @RequestParam List<Long> eventIds) {
        return analyticsService.getTotalOverview(eventIds);
    }

}
