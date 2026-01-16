package com.openticket.admin.controller.admin;

import com.openticket.admin.dto.AdminOrderListDTO;
import com.openticket.admin.service.AdminOrderService;

import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

// 管理者訂單API控制器
// 處理訂單列表的非同步查詢

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderApiController {

    private final AdminOrderService service;

    /**
     * 建構子注入 (Spring 4.3+ 之後若只有單一建構子可省略 @Autowired)
     * 
     * @param service 訂單管理業務邏輯層
     */
    public AdminOrderApiController(AdminOrderService service) {
        this.service = service;
    }

    /**
     * 多條件分頁查詢訂單列表
     * @param keyword   搜尋關鍵字（可能是訂單編號或用戶名）
     * @param startDate 訂單日期起點
     * @param endDate   訂單日期終點
     * @param page      分頁索引 (由 0 開始)
     * @param size      每頁筆數 (預設 10 筆)
     * @return 封裝後的分頁 DTO 數據
     */
    @GetMapping
    public Page<AdminOrderListDTO> listAdminOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.searchOrders(keyword, startDate, endDate, page, size);
    }
}
