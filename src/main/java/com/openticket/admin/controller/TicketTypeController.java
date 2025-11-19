package com.openticket.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.openticket.admin.entity.TicketType;
import com.openticket.admin.service.TicketTypeService;

@RestController
@RequestMapping("/api/tickets")
public class TicketTypeController {

    @Autowired
    private TicketTypeService service;

    // 查全部票種（屬於當前主辦方的）
    @GetMapping
    public List<TicketType> getAll() {
        return service.getAll();
    }

    // 新增票種
    @PostMapping
    public TicketType create(@RequestBody TicketType tt) {
        return service.create(tt);
    }

    // 修改票種
    @PutMapping("/{id}")
    public TicketType update(
            @PathVariable Long id,
            @RequestBody TicketType tt) {
        return service.update(id, tt);
    }

    // 刪除票種
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
