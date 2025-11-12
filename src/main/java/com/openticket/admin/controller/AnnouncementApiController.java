package com.openticket.admin.controller;

import com.openticket.admin.entity.Announcement;
import com.openticket.admin.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementApiController {

    @Autowired
    private AnnouncementService service;

    @GetMapping
    public List<Announcement> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Announcement create(@RequestBody Announcement ann) {
        return service.create(ann);
    }
}
