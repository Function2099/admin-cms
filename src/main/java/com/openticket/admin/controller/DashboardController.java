package com.openticket.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openticket.admin.service.AnnouncementService;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long count = announcementService.count();
        model.addAttribute("view", "dashboard");
        model.addAttribute("announcementCount", count);
        model.addAttribute("eventCount", 0);
        model.addAttribute("ticketCount", 0);
        return "index";
    }

    @GetMapping("/dashboard/announcement")
    public String announcementPage(Model model) {
        model.addAttribute("view", "fragments/announcement"); // 或 dashboard layout
        return "index"; // index.html 包 sidebar & main-content
    }

    @GetMapping("/content/announcement")
    public String announcementFragment(Model model) {
        model.addAttribute("pageTitle", "公告管理");
        return "fragments/announcement :: content";
    }

    @GetMapping("/dashboard/event")
    public String eventPage(Model model) {
        model.addAttribute("view", "fragments/event"); // 或 dashboard layout
        return "index"; // index.html 包 sidebar & main-content
    }

    @GetMapping("/content/event")
    public String eventFragment(Model model) {
        model.addAttribute("pageTitle", "活動管理");
        return "fragments/event :: content";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
