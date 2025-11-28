package com.openticket.admin.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openticket.admin.dto.EventTicketRequest;
import com.openticket.admin.entity.Event;
import com.openticket.admin.entity.EventTicketType;
import com.openticket.admin.entity.TicketDiscountConfig;
import com.openticket.admin.entity.TicketType;
import com.openticket.admin.repository.EventTicketTypeRepository;
import com.openticket.admin.repository.TicketDiscountConfigRepository;
import com.openticket.admin.repository.TicketTypeRepository;

@Service
@Transactional
public class EventTicketTypeService {

    @Autowired
    private EventTicketTypeRepository repo;

    @Autowired
    private TicketTypeRepository ticketTypeRepo;

    @Autowired
    private TicketDiscountConfigRepository discountRepo;

    public void createForEvent(Event event, List<EventTicketRequest> ticketList) {

        for (EventTicketRequest req : ticketList) {

            TicketType template = ticketTypeRepo.findById(req.getTicketTemplateId())
                    .orElseThrow(() -> new RuntimeException("找不到模板票種 ID：" + req.getTicketTemplateId()));

            EventTicketType ett = new EventTicketType();
            ett.setEvent(event);
            ett.setTicketTemplate(template);

            // 活動票價：活動覆蓋 > 模板 > null
            ett.setCustomPrice(
                    req.getCustomPrice() != null
                            ? req.getCustomPrice()
                            : template.getPrice());

            // 活動限量：活動覆蓋 > 模板 > null
            Integer finalLimit = req.getCustomLimit() != null
                    ? req.getCustomLimit()
                    : template.getLimitQuantity();

            ett.setCustomLimit(finalLimit);

            // isLimited 自動判斷
            ett.setIsLimited(finalLimit != null);

            // -------------------- 早鳥票設定 --------------------
            if (req.getIsEarlyBird() != null && req.getIsEarlyBird()) {

                ett.setIsEarlyBird(true);

                // 優惠折扣轉成 0.xx
                BigDecimal discountRate = req.getDiscountRate()
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                // 建立早鳥設定
                TicketDiscountConfig config = new TicketDiscountConfig();
                config.setDiscountPrice(discountRate);
                config.setDurationDays(req.getEarlyBirdDays());

                discountRepo.save(config);
                ett.setEarlyBirdConfig(config);
            } else {
                ett.setIsEarlyBird(false);
                ett.setEarlyBirdConfig(null);
            }
            repo.save(ett);
        }
    }

    public void rebuildEventTickets(Event event, List<EventTicketRequest> list) {

        // 1. 先刪除舊票種
        repo.deleteByEvent(event);

        // 2. 重新新增
        for (EventTicketRequest req : list) {

            TicketType template = ticketTypeRepo.findById(req.getTicketTemplateId())
                    .orElseThrow(() -> new RuntimeException("找不到模板票種 ID：" + req.getTicketTemplateId()));

            EventTicketType ett = new EventTicketType();
            ett.setEvent(event);
            ett.setTicketTemplate(template);

            // 活動票價
            ett.setCustomPrice(
                    req.getCustomPrice() != null
                            ? req.getCustomPrice()
                            : template.getPrice());

            // 活動限量：活動覆蓋 > 模板 > null
            Integer finalLimit = req.getCustomLimit() != null
                    ? req.getCustomLimit()
                    : template.getLimitQuantity();

            ett.setCustomLimit(finalLimit);
            ett.setIsLimited(finalLimit != null);

            // 早鳥票相關
            if (Boolean.TRUE.equals(req.getIsEarlyBird())) {

                ett.setIsEarlyBird(true);

                // BigDecimal 轉百分比
                BigDecimal discountRate = req.getDiscountRate()
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                TicketDiscountConfig config = new TicketDiscountConfig();
                config.setDiscountPrice(discountRate);
                config.setDurationDays(req.getEarlyBirdDays());

                // 儲存早鳥設定
                discountRepo.save(config);
                ett.setEarlyBirdConfig(config);

            } else {
                ett.setIsEarlyBird(false);
                ett.setEarlyBirdConfig(null);
            }

            repo.save(ett);
        }
    }

    public List<EventTicketRequest> findByEventId(Long eventId) {
        List<EventTicketType> entities = repo.findByEventId(eventId);

        return entities.stream().map(e -> {
            EventTicketRequest dto = new EventTicketRequest();
            dto.setTicketTemplateId(e.getTicketTemplate().getId());
            dto.setCustomPrice(e.getCustomPrice());
            dto.setCustomLimit(e.getCustomLimit());
            dto.setDescription(e.getTicketTemplate().getDescription());

            // 早鳥啟用
            dto.setIsEarlyBird(e.getIsEarlyBird());

            // 若有早鳥設定
            if (e.getEarlyBirdConfig() != null) {
                dto.setEarlyBirdDays(e.getEarlyBirdConfig().getDurationDays());
                // discountRate 是前端百分比 → 後端 BigDecimal 小數要乘 100
                dto.setDiscountRate(
                        e.getEarlyBirdConfig().getDiscountPrice().multiply(new BigDecimal("100")));
            } else {
                dto.setEarlyBirdDays(null);
                dto.setDiscountRate(null);
            }
            return dto;
        }).toList();
    }

}
