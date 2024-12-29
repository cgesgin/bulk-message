package com.cgesgin.bulkmessage.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    @Value("${whatsapp.service.url}")
    private String url;


    @GetMapping
    public String showDashboard(Model model) {
        String dashboardUrl = this.url + "/dashboard";
        model.addAttribute("dashboardUrl", dashboardUrl);
        return "dashboard/index";
    }
}