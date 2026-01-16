package com.openticket.admin.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openticket.admin.entity.User;
import com.openticket.admin.service.AdminUserService;

/**
 * 管理員用戶管理 API 控制器
 * 處理用戶權限變更、狀態啟用的非同步操作
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

    private final AdminUserService service;

    public AdminUserApiController(AdminUserService service) {
        this.service = service;
    }

    /**
     * 獲取用戶分頁列表
     * 
     * @param keyword 搜尋關鍵字 (用戶名/Email)
     * @param page    當前頁碼
     * @param size    每頁顯示數量
     * @return 返回分頁後的用戶資料 (注意：生產環境應轉換為 UserDTO)
     */
    @GetMapping
    public Page<User> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.searchUsers(keyword, page, size);
    }

    /**
     * 修改用戶權限角色
     * 
     * @param id   用戶唯一標識
     * @param role 角色代碼 (建議與後端 Enum 對應)
     */
    @PutMapping("/{id}/role")
    public void updateRole(
            @PathVariable Long id,
            @RequestParam int role) {
        service.updateRole(id, role);
    }

    /**
     * 修改用戶啟用狀態
     * 
     * @param id     用戶唯一標識
     * @param active 狀態值 (1:啟用, 0:禁用)
     */
    @PutMapping("/{id}/active")
    public void updateActive(
            @PathVariable Long id,
            @RequestParam int active) {
        service.updateActive(id, active);
    }
}
