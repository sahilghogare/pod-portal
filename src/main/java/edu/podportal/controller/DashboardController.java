package edu.podportal.controller;

import edu.podportal.service.DeliveryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DeliveryService service;

    public DashboardController(DeliveryService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/summary")
    public String dashboard(Model model) {
        Map<String, Long> counts = service.statusCounts();

        long total = counts.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        model.addAttribute("statusCounts", counts);
        model.addAttribute("totalDeliveries", total);

        return "dashboard";
    }
}