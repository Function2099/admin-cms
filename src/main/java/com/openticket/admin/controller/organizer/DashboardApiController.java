package com.openticket.admin.controller.organizer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openticket.admin.security.LoginCompanyProvider;
import com.openticket.admin.service.DashboardService;

/**
 * 儀表板數據 API 控制器
 * 專門提供主辦方後台首頁所需的關鍵績效指標 (KPI) 數據
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    @Autowired
    private DashboardService dashboardService;

    // 用於獲取當前登入主辦方資訊的供應器 (Provider)
    @Autowired
    private LoginCompanyProvider loginCompanyProvider;

    /**
     * 獲取主辦方核心 KPI 數據 (如：今日營收、售票率、活動數)
     * 
     * @return 包含 success 狀態與 data 數據體的 Map
     */
    @GetMapping("/kpi")
    public Map<String, Object> getKpi() {

        // 1. 透過 Provider 獲取目前登入者的主辦方 ID (封裝了 Session 獲取細節)
        Long companyId = loginCompanyProvider.getCompanyId();

        // 2. 調用 Service 層進行大數據聚合運算
        Map<String, Object> kpi = dashboardService.getOrganizerKpi(companyId);

        // 3. 返回標準格式的結果 (建議未來優化為 ResultDTO)
        return Map.of(
                "success", true,
                "data", kpi);
    }
}
