package com.openticket.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.openticket.admin.dto.EventListItemDTO;
import com.openticket.admin.entity.Event;
import com.openticket.admin.entity.EventStats;
import com.openticket.admin.repository.CheckoutOrderRepository;
import com.openticket.admin.repository.EventRepository;
import com.openticket.admin.repository.EventStatsRepository;

@Service
public class DashboardService {

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private EventStatsRepository eventStatsRepository;

        @Autowired
        private CheckoutOrderRepository checkoutOrderRepository;

        /* 取得該最新 3 筆活動 */
        public List<EventListItemDTO> getLatestEvents(Long companyId) {

                // 取得活動
                List<Event> events = eventRepository.findByCompanyUser_Id(
                                companyId,
                                Sort.by(Sort.Direction.DESC, "createdAt"));

                return events.stream()
                                .limit(3)
                                .map(this::toDTO)
                                .toList();
        }

        // ========== 封裝：Event -> EventListItemDTO ==========
        private EventListItemDTO toDTO(Event e) {

                EventListItemDTO dto = new EventListItemDTO();

                dto.setId(e.getId());
                dto.setTitle(e.getTitle());
                dto.setEventStart(e.getEventStartFormatted());
                dto.setEventEnd(e.getEventEndFormatted());
                dto.setTicketStart(e.getTicketStartFormatted());
                dto.setCreatedAt(e.getCreatedAtIso());
                dto.setStatus(e.getDynamicStatus());
                dto.setImages(e.getImages());

                // ===== 從 event_stats 拿流量 & 分享 =====
                EventStats stats = eventStatsRepository.findById(e.getId()).orElse(null);
                dto.setViews(stats != null ? stats.getViews() : 0);
                dto.setShares(stats != null ? stats.getShares() : 0);

                // ===== 從 checkout_orders 取得售出票數 + 總營收 =====
                List<Object[]> rows = checkoutOrderRepository.sumTicketsAndRevenueByEvent(e.getId());

                if (rows != null && !rows.isEmpty()) {
                        Object[] row = rows.get(0);

                        dto.setTicketsSold(
                                        row[0] != null ? ((Number) row[0]).intValue() : 0);

                        dto.setRevenue(
                                        row[1] != null ? ((java.math.BigDecimal) row[1]).longValue() : 0L);

                } else {
                        dto.setTicketsSold(0);
                        dto.setRevenue(0L);
                }

                return dto;
        }

        public Map<String, Object> getOrganizerKpi(Long companyId) {

                // 1. 查全部活動
                List<Event> events = eventRepository.findByCompanyUser_Id(companyId);
                List<Long> eventIds = events.stream().map(Event::getId).toList();

                if (eventIds.isEmpty()) {
                        return Map.of(
                                        "viewsTotal", 0,
                                        "ticketsTotal", 0,
                                        "revenueTotal", 0);
                }

                // ---------【方案 C：一次迴圈全部算完】---------
                long viewsTotal = 0;
                long ticketsTotal = 0;
                long revenueTotal = 0;

                for (Long eventId : eventIds) {

                        // A. 流量（event_stats）
                        EventStats stats = eventStatsRepository.findById(eventId).orElse(null);
                        if (stats != null) {
                                viewsTotal += stats.getViews();
                        }

                        // B. 售票 + 營收（checkout_orders）
                        List<Object[]> rows = checkoutOrderRepository.sumTicketsAndRevenueByEvent(eventId);

                        if (rows != null && !rows.isEmpty()) {
                                Object[] row = rows.get(0);

                                long tickets = row[0] != null ? ((Number) row[0]).longValue() : 0;
                                long revenue = row[1] != null ? ((java.math.BigDecimal) row[1]).longValue() : 0;

                                ticketsTotal += tickets;
                                revenueTotal += revenue;
                        }
                }

                // 3. 回傳
                return Map.of(
                                "viewsTotal", viewsTotal,
                                "ticketsTotal", ticketsTotal,
                                "revenueTotal", revenueTotal);
        }

}
