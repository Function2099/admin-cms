package com.openticket.admin.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openticket.admin.controller.BaseController;
import com.openticket.admin.entity.Role;

import jakarta.servlet.http.HttpSession;

// 管理員後台層
// 負責處理管理員權限的頁面路由與(Fragment)頁面片段

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    /**
     * 後台主儀表板整頁路由
     * 
     * @param model   Spring MVC 模型，用於傳遞數據至視圖
     * @param session HTTP 會話，用於權限與狀態校驗
     * @return 返回 index 模板，並動態指定內容區域為 dashboard 片段
     */

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // 執行權限校驗與角色初始化 (繼承自 BaseController)
        setupRole(model, session, Role.ADMIN);
        // 設定 Thymeleaf 佈局中的動態片段路徑
        model.addAttribute("content", "fragments/admin/admin-dashboard :: content");
        return "index";
    }

    // 使用者管理頁面
    @GetMapping("/dashboard/users")
    public String users(Model model, HttpSession session) {
        setupRole(model, session, Role.ADMIN);
        model.addAttribute("content", "fragments/admin/users :: content");
        return "index";
    }

    // 訂單管理(管理者)頁面
    @GetMapping("/dashboard/orders")
    public String orders(Model model, HttpSession session) {
        setupRole(model, session, Role.ADMIN);
        model.addAttribute("content", "fragments/admin/orders :: content");
        return "index";
    }

    /**
     * 獲取後台儀表板片段 (用於 AJAX 局部更新)
     * @return 僅返回 Thymeleaf 片段路徑，不含 index 佈局
     */
    @GetMapping("/dashboard-frag")
    public String dashboardFrag(Model model) {
        return "fragments/admin/admin-dashboard :: content";
    }

    // 獲取使用者管理片段
    @GetMapping("/dashboard/users-frag")
    public String usersFrag(Model model) {
        return "fragments/admin/users :: content";
    }

    // 獲取使用者管理片段
    @GetMapping("/dashboard/orders-frag")
    public String adminOrdersFrag(Model model) {
        return "fragments/admin/orders :: content";
    }

}
