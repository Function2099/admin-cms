package com.openticket.admin.controller.organizer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openticket.admin.dto.EventListItemDTO;
import com.openticket.admin.dto.EventTitleDTO;
import com.openticket.admin.entity.Event;
import com.openticket.admin.security.LoginCompanyProvider;
import com.openticket.admin.service.DashboardService;
import com.openticket.admin.service.event.EventCreationService;
import com.openticket.admin.service.event.EventQueryService;
import com.openticket.admin.service.event.EventService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 活動管理核心 API 控制器
 * 負責活動的 CRUD、狀態控制、圖片上傳及分頁查詢
 */
@RestController
@RequestMapping("/api/events")
public class EventApiController {

    /**
     * 安全性控制：禁止前端透過自動綁定修改敏感欄位
     * 避免 Mass Assignment 攻擊
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(
                "id",
                "companyUser",
                "status",
                "createdAt",

                "images",
                "images.*.id",
                "images.*.imageUrl",
                "images.*.createdAt",

                // 活動票種
                "eventTicketTypes",
                "eventTicketTypes.*.id",
                "eventTicketTypes.*.eventId",
                "eventTicketTypes.*.createdAt",
                "eventTicketTypes.*.ticketTemplateId",
                "eventTicketTypes.*.earlyBirdConfig",
                "eventTicketTypes.*.earlyBirdConfigId");
    }

    @Autowired
    private EventService eventService;

    @Autowired
    private EventQueryService eventQueryService;

    @Autowired
    private EventCreationService eventCreationService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private LoginCompanyProvider loginCompanyProvider;

    /**
     * 多條件分頁查詢活動列表
     * 包含關鍵字搜尋與動態狀態 DTO 轉換
     */
    @GetMapping
    public Page<EventListItemDTO> getPagedEvents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order) {

        Long companyId = loginCompanyProvider.getCompanyId();
        // 呼叫 Service 的新方法
        return eventService.searchEvents(companyId, page, size, keyword, sort, order);
    }

    @GetMapping("/latest")
    public List<EventListItemDTO> getLatestEvents() {
        Long companyId = loginCompanyProvider.getCompanyId();
        return dashboardService.getLatestEvents(companyId);
    }

    /**
     * 建立新活動
     * 處理 Multipart (圖片) 與 JSON 混合數據
     */
    @PostMapping("/create")
    public ResponseEntity<?> createEvent(
            @ModelAttribute Event event,
            @RequestParam("cover") MultipartFile coverFile,
            HttpServletRequest request) {

        try {
            String ticketJson = request.getParameter("eventTicketsJson");
            String description = request.getParameter("description");

            Event saved = eventCreationService.createEventWithAll(
                    event, coverFile, ticketJson, description);

            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("活動建立失敗：" + e.getMessage());
        }
    }

    /**
     * 更新活動資訊
     * 包含嚴格的業務檢核：活動進行中不可修改關鍵時間
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Long companyId = loginCompanyProvider.getCompanyId();
        Map<String, Object> result = eventService.getEventDetails(id, companyId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @ModelAttribute Event updated,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            HttpServletRequest request) {

        try {
            Long companyId = loginCompanyProvider.getCompanyId();
            String description = request.getParameter("description");
            String ticketJson = request.getParameter("eventTicketsJson");

            Map<String, Object> response = eventService.executeEventUpdate(
                    id, companyId, updated, coverFile, description, ticketJson);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            // 捕獲業務邏輯驗證錯誤 (如：狀態不可編輯)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新活動失敗：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelEvent(@PathVariable Long id) {
        Long companyId = loginCompanyProvider.getCompanyId();

        try {
            eventService.executeEventCancellation(id, companyId);
            return ResponseEntity.ok("活動已取消");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("取消失敗");
        }
    }

    @GetMapping("/my")
    public List<EventTitleDTO> getMyEventTitles(
            @RequestParam(required = false) String keyword) {

        Long companyId = loginCompanyProvider.getCompanyId(); // TODO JWT
        return eventQueryService.getEventTitles(companyId, keyword);
    }
}