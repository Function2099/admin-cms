package com.openticket.admin.service.event;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.openticket.admin.dto.EventListItemDTO;
import com.openticket.admin.dto.EventTicketRequest;
import com.openticket.admin.entity.Event;
import com.openticket.admin.entity.EventDetail;
import com.openticket.admin.entity.EventStats;
import com.openticket.admin.entity.EventStatus;
import com.openticket.admin.entity.EventTitlePage;
import com.openticket.admin.repository.EventDetailRepository;
import com.openticket.admin.repository.EventRepository;
import com.openticket.admin.repository.EventStatsRepository;
import com.openticket.admin.repository.EventStatusRepository;
import com.openticket.admin.service.SmbStorageService;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatsRepository eventStatsRepository;

    @Autowired
    private EventDetailRepository detailRepo;

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    @Lazy // 使用 Lazy 避免潛在的循環依賴
    private EventTicketTypeService eventTicketTypeService;

    @Autowired
    private SmbStorageService smbStorageService;

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到活動 ID=" + id));
    }

    @Transactional
    public void save(Event event) {
        eventRepository.save(event);
    }

    @Transactional
    public void updateDetail(Event event, String content) {

        // 找 event_detail
        EventDetail detail = detailRepo.findByEvent(event).orElse(null);

        if (content == null || content.isBlank()) {
            // 若無內容 → 不建立詳細內容
            return;
        }

        if (detail == null) {
            // 新增
            detail = new EventDetail();
            detail.setEvent(event);
        }

        detail.setContent(content);
        detailRepo.save(detail);
    }

    public List<Event> getAllEvents(Long companyId) {
        return eventRepository.findByCompanyUser_Id(companyId);
    }

    public Event getEventById(Long id, Long companyId) {
        return eventRepository.findByIdAndCompanyUserId(id, companyId)
                .orElseThrow(() -> new RuntimeException("活動不存在或沒有權限"));
    }

    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Event updated) {
        return eventRepository.save(updated);
    }

    public List<Map<String, Object>> getAllEventsWithStats() {
        List<Event> events = eventRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Event e : events) {
            Map<String, Object> map = new HashMap<>();
            // ... 填入 event 欄位

            // 查詢對應的統計資料（可能為 null）
            EventStats stats = eventStatsRepository.findById(e.getId()).orElse(null);

            map.put("views", stats != null ? stats.getViews() : 0);
            map.put("shares", stats != null ? stats.getShares() : 0);
            map.put("ticketsSold", getTicketsSold(e.getId())); // 動態計算

            result.add(map);
        }
        return result;
    }

    private int getTicketsSold(Long id) {
        return (int) ((id * 13 % 1500) + 1);
    }

    public Page<EventListItemDTO> getEventListPage(Long companyId, String keyword, Pageable pageable) {

        Page<Event> events;

        // 搜尋 or 全部
        if (keyword == null || keyword.isBlank()) {
            events = eventRepository.findByCompanyUserId(companyId, pageable);
        } else {
            events = eventRepository.searchByCompanyUserId(companyId, "%" + keyword + "%", pageable);
        }

        // 轉 DTO
        return events.map(ev -> {
            EventListItemDTO item = new EventListItemDTO();
            item.setId(ev.getId());
            item.setTitle(ev.getTitle());
            item.setEventStart(ev.getEventStartFormatted());
            item.setEventEnd(ev.getEventEndFormatted());
            item.setTicketStart(ev.getTicketStartFormatted());
            item.setCreatedAt(ev.getCreatedAtIso());
            item.setStatus(calculateDynamicStatus(ev));

            // 假資料
            item.setViews(0);
            item.setTicketsSold(getTicketsSold(ev.getId()));

            return item;
        });
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public String calculateDynamicStatus(Event event) {
        if (event.getStatus() != null && "已取消".equals(event.getStatus().getStatus())) {
            return "已取消";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sale = event.getTicketStart();
        LocalDateTime start = event.getEventStart();
        LocalDateTime end = event.getEventEnd();

        if (sale == null || start == null || end == null) {
            return event.getStatus() != null ? event.getStatus().getStatus() : "未設定";
        }

        if (now.isBefore(sale))
            return "未開放";
        if (now.isBefore(start))
            return "開放購票";
        if (now.isBefore(end))
            return "活動進行中";
        return "已結束";
    }

    public Event findOwnedEvent(Long eventId, Long companyId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("找不到活動 ID=" + eventId));

        if (!event.getCompanyUser().getId().equals(companyId)) {
            throw new RuntimeException("無權限存取此活動");
        }

        return event;
    }

    /**
     * 處理分頁查詢業務邏輯
     */
    public Page<EventListItemDTO> searchEvents(Long companyId, int page, int size, String keyword, String sort,
            String order) {
        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(direction, sort));

        Page<Event> eventPage;

        if (keyword == null || keyword.isBlank()) {
            eventPage = eventRepository.findByCompanyUserId(companyId, pageable);
        } else {
            eventPage = eventRepository.searchByCompanyUserId(
                    companyId,
                    "%" + keyword + "%",
                    pageable);
        }

        // 轉成 DTO
        return eventPage.map(event -> {
            EventListItemDTO dto = new EventListItemDTO();
            dto.setId(event.getId());
            dto.setTitle(event.getTitle());
            dto.setEventStart(event.getEventStartFormatted());
            dto.setEventEnd(event.getEventEndFormatted());
            dto.setTicketStart(event.getTicketStartFormatted());
            dto.setCreatedAt(event.getCreatedAtIso());
            dto.setStatus(this.calculateDynamicStatus(event)); // 呼叫原本 service 內的方法
            dto.setViews(0);
            dto.setTicketsSold(0);
            dto.setImages(event.getImages());
            return dto;
        });
    }

    /**
     * 取得單一活動詳情與相關邏輯計算
     */
    public Map<String, Object> getEventDetails(Long eventId, Long companyId) {
        Event event = this.findOwnedEvent(eventId, companyId);

        // 活動描述
        EventDetail detail = detailRepo.findByEventId(event.getId()); // 注意：這裡原本 controller 用 eventDetailRepository，現在改用
                                                                      // service 注入的 detailRepo
        String description = detail != null ? detail.getContent() : "";

        List<EventTicketRequest> selectedTickets = eventTicketTypeService.findByEventId(event.getId());

        // 計算哪些票種已經有訂單，不可刪除
        List<Long> cannotDeleteIds = event.getEventTicketTypes().stream()
                .filter(e -> e.getCheckoutOrders() != null && !e.getCheckoutOrders().isEmpty())
                .map(e -> e.getTicketTemplate().getId())
                .toList();

        // 組合回傳 Map
        Map<String, Object> result = new HashMap<>();
        result.put("id", event.getId());
        result.put("title", event.getTitle());
        result.put("address", event.getAddress());
        result.put("eventStart", event.getEventStart());
        result.put("eventEnd", event.getEventEnd());
        result.put("ticketStart", event.getTicketStart());
        result.put("description", description);
        result.put("selectedTickets", selectedTickets);
        result.put("images", event.getImages());
        result.put("createdAt", event.getCreatedAtIso());

        result.put("cannotDeleteTicketIds", cannotDeleteIds);
        result.put("eventStatus", this.calculateDynamicStatus(event));

        return result;
    }

    /**
     * 執行活動更新 (包含驗證、檔案上傳、子資料更新)
     */
    @Transactional
    public Map<String, Object> executeEventUpdate(Long eventId, Long companyId, Event updatedData,
            MultipartFile coverFile, String description, String ticketJson) throws IOException {

        Event event = this.findOwnedEvent(eventId, companyId);

        // 1. 若活動不可編輯
        String status = this.calculateDynamicStatus(event);

        if ("已取消".equals(status) || "已結束".equals(status)) {
            throw new IllegalStateException("此活動狀態為「" + status + "」，不可編輯");
        }

        boolean isOngoing = "活動進行中".equals(status);

        // 2. 更新基本欄位
        if (isOngoing) {
            boolean eventStartChanged = updatedData.getEventStart() != null &&
                    !Objects.equals(event.getEventStart(), updatedData.getEventStart());

            boolean eventEndChanged = updatedData.getEventEnd() != null &&
                    !Objects.equals(event.getEventEnd(), updatedData.getEventEnd());

            boolean ticketStartChanged = updatedData.getTicketStart() != null &&
                    !Objects.equals(event.getTicketStart(), updatedData.getTicketStart());

            if (eventStartChanged || eventEndChanged || ticketStartChanged) {
                throw new IllegalStateException("活動進行中，不可修改任何時間");
            }
        }

        // 3. 永遠可修改的欄位
        event.setTitle(updatedData.getTitle());
        event.setAddress(updatedData.getAddress());

        // 4. 只有「未進行中」才允許改時間
        if (!isOngoing) {
            event.setEventStart(updatedData.getEventStart());
            event.setEventEnd(updatedData.getEventEnd());
            event.setTicketStart(updatedData.getTicketStart());
        }

        // 3. 更新 event_detail (呼叫原有方法)
        this.updateDetail(event, description);

        // 4. 更新票種
        if (ticketJson != null && !ticketJson.isBlank()) {
            ObjectMapper mapper = new ObjectMapper();
            List<EventTicketRequest> list = mapper.readValue(ticketJson, new TypeReference<List<EventTicketRequest>>() {
            });
            eventTicketTypeService.rebuildEventTickets(event, list);
        }

        // 5. 若有新封面 → 更新封面（覆蓋到 SMB）
        if (coverFile != null && !coverFile.isEmpty()) {

            // 先記錄舊檔名
            List<String> oldFilenames = event.getImages().stream()
                    .map(EventTitlePage::getImageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .map(url -> url.substring(url.lastIndexOf('/') + 1))
                    .toList();

            // 儲存新圖片到 SMB
            String filename = UUID.randomUUID() + "_" + coverFile.getOriginalFilename();
            smbStorageService.uploadCover(filename, coverFile.getInputStream());

            // 建立新的 EventTitlePage
            EventTitlePage page = new EventTitlePage();
            page.setImageUrl("/api/files/covers/" + filename);
            page.setEvent(event);

            // 刪除舊資料庫紀錄
            event.getImages().clear();
            event.getImages().add(page);

            // 刪除 SMB 上的舊檔
            for (String old : oldFilenames) {
                try {
                    smbStorageService.deleteCover(old);
                } catch (IOException ignore) {
                    // 忽略刪除錯誤
                }
            }
        }

        this.save(event);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("title", event.getTitle());
        return response;
    }

    /**
     * 執行活動取消
     */
    @Transactional
    public void executeEventCancellation(Long eventId, Long companyId) {
        Event event = this.findOwnedEvent(eventId, companyId);

        // 只能「未開放」才能取消 (狀態 ID 1 假設為未開放，依原本邏輯)
        if (event.getStatus().getId() != 1) {
            throw new IllegalStateException("只有「未開放」的活動可以取消");
        }

        // 取 id=5 的狀態（已取消）
        EventStatus canceled = eventStatusRepository.findById(5L)
                .orElseThrow(() -> new RuntimeException("找不到取消狀態"));

        event.setStatus(canceled);
        this.save(event);
    }
}
