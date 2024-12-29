package com.cgesgin.bulkmessage.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Value("${whatsapp.service.url}")
    private String whatsappUrl;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboardUrl", whatsappUrl + "/dashboard");
        return "dashboard/index";
    }
}