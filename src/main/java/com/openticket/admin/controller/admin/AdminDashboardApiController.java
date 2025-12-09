package com.openticket.admin.controller.admin;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openticket.admin.dto.AnalyticsDTO;
import com.openticket.admin.entity.LoginLog;
import com.openticket.admin.repository.HomepageSessionLogRepository;
import com.openticket.admin.service.AnalyticsService;
import com.openticket.admin.service.LoginLogService;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardApiController {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private HomepageSessionLogRepository homepageRepo;

    @GetMapping("/login-logs")
    public Page<LoginLog> getLoginLogs(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return loginLogService.searchLoginLogs(keyword, pageable);
    }

    @GetMapping("/dashboard-analytics")
    public Map<String, Object> getDashboardAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        // 全平台活動 IDs
        List<Long> eventIds = analyticsService.getAllEventIds();

        // 日期解析
        LocalDate s = (startDate == null || startDate.isEmpty())
                ? null
                : LocalDate.parse(startDate);

        LocalDate e = (endDate == null || endDate.isEmpty())
                ? null
                : LocalDate.parse(endDate);

        // 查詢合併模式
        AnalyticsDTO analytics = analyticsService.getAnalytics(eventIds, "merge", s, e);

        // KPI：首頁總瀏覽量
        long homepageViews = homepageRepo.count();

        // 回傳物件
        Map<String, Object> result = new HashMap<>();
        result.put("homepageViews", homepageViews);
        result.put("analytics", analytics);

        return result;
    }
}
