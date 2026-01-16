package com.openticket.admin.controller.organizer;

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

/**
 * 數據分析 API 控制器
 * 提供針對特定活動 (Events) 的營收、人次及趨勢分析數據
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsApiController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * 取得多活動的詳細分析數據
     * 
     * @param eventIds  活動 ID 列表 (對應資料庫多筆活動)
     * @param mode      統計模式 (例如 "merge" 合併統計, "separate" 獨立統計)
     * @param startDate 統計起始日期 (ISO 格式：YYYY-MM-DD)
     * @param endDate   統計結束日期
     * @return 包含趨勢圖表、分布情況的 AnalyticsDTO
     */
    @GetMapping
    public AnalyticsDTO getAnalytics(
            @RequestParam List<Long> eventIds,
            @RequestParam(defaultValue = "merge") String mode,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return analyticsService.getAnalytics(eventIds, mode, startDate, endDate);
    }

    /**
     * 取得活動總覽統計數據
     * 
     * @param eventIds 活動 ID 列表
     * @return 包含總營收、總票數等核心指標的內部 DTO
     */
    @GetMapping("/overview")
    public AnalyticsDTO.Overview getTotalOverview(
            @RequestParam List<Long> eventIds) {
        return analyticsService.getTotalOverview(eventIds);
    }

}
