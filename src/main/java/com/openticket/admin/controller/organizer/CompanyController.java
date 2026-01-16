package com.openticket.admin.controller.organizer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openticket.admin.controller.BaseController;
import com.openticket.admin.entity.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

// 主辦方後台層
// 負責處理主辦方的頁面路由與(Fragment)頁面片段
@Controller
@RequestMapping("/organizer")
public class CompanyController extends BaseController {

    /**
     * 後台主儀表板
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        setupRole(model, session, Role.COMPANY);
        model.addAttribute("content", "fragments/dashboard :: content");
        return "index";
    }

    /**
     * 動態子路徑處理器
     * 攔截所有 /organizer/dashboard/ 下的請求，根據 URI 決定嵌入哪個 Thymeleaf 片段
     */
    @GetMapping("/dashboard/**")
    public String dashboardSub(HttpServletRequest request, Model model, HttpSession session) {
        setupRole(model, session, Role.COMPANY);
        String path = request.getRequestURI();
        
        // 提取相對路徑以匹配對應的視圖片段
        String subPath = path.replace("/organizer/dashboard/", "");
        String fragmentPath;

        // 所有 event/edit/** 都丟到 event fragment
        if (subPath.startsWith("event/edit")) {
            fragmentPath = "fragments/event :: content";
        } else {
            switch (subPath) {
                case "event":
                case "event/ticket":
                    fragmentPath = "fragments/event :: content";
                    break;
                case "orders":
                    fragmentPath = "fragments/orders :: content";
                    break;
                case "analytics/traffic":
                    fragmentPath = "fragments/analytics/traffic :: content";
                    break;
                case "analytics/consumer":
                    fragmentPath = "fragments/analytics/consumer :: content";
                    break;

                default:
                    fragmentPath = "fragments/dashboard :: content";
                    break;
            }
        }

        model.addAttribute("content", fragmentPath);
        return "index";
    }

    // =============活動=============

    @GetMapping("/dashboard-frag")
    public String dashboardFragment() {
        return "fragments/dashboard :: content";
    }

    @GetMapping("/event-frag")
    public String eventFragment(Model model) {
        return "fragments/event :: content";
    }

    // =============數據分析=============
    @GetMapping("/analytics/traffic-frag")
    public String analyticsTraffic() {
        return "fragments/analytics/traffic :: content";
    }

    @GetMapping("/analytics/consumer-frag")
    public String analyticsConsumer() {
        return "fragments/analytics/consumer :: content";
    }

    // =============訂單紀錄=============
    @GetMapping("/orders-frag")
    public String ordersHistory() {
        return "fragments/orders :: content";
    }

    // =============測試端=============
    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
