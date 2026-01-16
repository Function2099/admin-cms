package com.openticket.admin.controller.admin;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openticket.admin.dto.AdminAnalyticsDTO;
import com.openticket.admin.entity.LoginLog;
import com.openticket.admin.service.AnalyticsService;
import com.openticket.admin.service.LoginLogService;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardApiController {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * 取得登錄日誌分頁列表
     * 
     * @param keyword 搜尋關鍵字（用戶名或 IP）
     * @param page    當前頁碼 (0 開始)
     * @param size    每頁顯示筆數
     * @return 包含分頁資訊與日誌數據的 Page 對象
     */

    @GetMapping("/login-logs")
    public Page<LoginLog> getLoginLogs(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 封裝分頁參數請求對象
        Pageable pageable = PageRequest.of(page, size);
        return loginLogService.searchLoginLogs(keyword, pageable);
    }

    /**
     * 取得後台儀表板統計分析數據
     * 
     * @param startDate 統計起始日期 (格式: ISO-8601 YYYY-MM-DD)
     * @param endDate   統計結束日期
     * @return 封裝後的統計數據傳輸對象 (DTO)
     */
    @GetMapping("/dashboard-analytics")
    public AdminAnalyticsDTO getAdminAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return analyticsService.getAdminAnalytics(startDate, endDate);
    }

}
