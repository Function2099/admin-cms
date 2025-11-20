package com.openticket.admin.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openticket.admin.dto.TicketTypeDto;
import com.openticket.admin.entity.TicketType;
import com.openticket.admin.entity.User;
import com.openticket.admin.repository.TicketTypeRepository;

@Service
public class TicketTypeService {

    @Autowired
    private TicketTypeRepository repo;

    public List<TicketType> getAll() {
        return repo.findAll();
    }

    public TicketType create(TicketType tt) {

        User currentUser = new User();
        currentUser.setId(2L);
        if (Boolean.FALSE.equals(tt.getIsDefault())) {
            // 系統模板 → user_id 必須 null
            tt.setUser(null);
        } else {
            // 主辦方自訂 → 必須有登入的主辦方
            // if (currentUser == null) {
            // throw new RuntimeException("自訂票種需要主辦方帳號");
            // }
            tt.setUser(currentUser);
        }

        return repo.save(tt);
    }

    // 🔵 新增：取得「系統模板」
    public List<TicketType> getTemplates() {
        return repo.findByIsDefaultFalse();
    }

    // 🔵 新增：取得某主辦方的自訂票
    public List<TicketType> getCustom(Long userId) {
        return repo.findByIsDefaultTrueAndUserId(userId);
    }

    // 🔵 新增：活動用 → 模板 + 自訂票
    public List<TicketType> getAllForOrganizer(Long userId) {
        List<TicketType> result = new ArrayList<>();
        result.addAll(getTemplates());
        result.addAll(getCustom(userId));
        return result;
    }

    public TicketType update(Long id, TicketType newData) {
        TicketType tt = repo.findById(id).orElseThrow(() -> new RuntimeException("票種不存在 ID=" + id));

        tt.setName(newData.getName());
        tt.setPrice(newData.getPrice());
        tt.setIsLimited(newData.getIsLimited());
        tt.setLimitQuantity(newData.getLimitQuantity());

        return repo.save(tt);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<TicketTypeDto> getAllDtos() {
        return getAll().stream()
                .map(tt -> new TicketTypeDto(
                        tt.getId(),
                        tt.getName(),
                        tt.getPrice(),
                        tt.getIsLimited(),
                        tt.getLimitQuantity(),
                        tt.getDescription()))
                .toList();
    }
}
