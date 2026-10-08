package edu.podportal.controller;

import edu.podportal.model.Delivery;
import edu.podportal.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("deliveries", service.findAll());
        return "deliveries/list";
    }

    @GetMapping("/new")
    public String newDelivery(Model model) {
        model.addAttribute("delivery", new Delivery());
        return "deliveries/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("delivery") Delivery delivery,
            BindingResult result) {

        if (service.trackingNumberExists(delivery.getTrackingNo())) {
            result.rejectValue(
                    "trackingNo",
                    "duplicate",
                    "Tracking number already exists"
            );
        }

        if (result.hasErrors()) {
            return "deliveries/form";
        }

        Delivery saved = service.create(delivery);

        return "redirect:/deliveries/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("delivery", service.findById(id));
        return "deliveries/detail";
    }
}