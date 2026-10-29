package edu.podportal.controller;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.model.OtpStatus;
import edu.podportal.service.DeliveryService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import edu.podportal.service.OtpService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;
    private final OtpService otpService;

    public DeliveryController(DeliveryService service, OtpService otpService) {
        this.service = service;
        this.otpService = otpService;
    }


    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String trackingNo,
            @RequestParam(defaultValue = "") String customer,
            @RequestParam(defaultValue = "") String status,
            Model model,
            Authentication authentication) {

        model.addAttribute(
                "deliveries",
                service.search(trackingNo, customer, status));

        model.addAttribute("trackingNo", trackingNo);
        model.addAttribute("customer", customer);
        model.addAttribute("selectedStatus", status);
        model.addAttribute(
                "statuses", Arrays.asList(DeliveryStatus.values()));

        boolean isAdmin = authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("canCreateDelivery", isAdmin);
        model.addAttribute("canEditDelivery", isAdmin);

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
            BindingResult result,
            @RequestParam(name = "adminOtp", required = false) String adminOtp,
            @RequestParam(name = "generateOtp", defaultValue = "false") boolean generateOtp,
            RedirectAttributes redirectAttributes) {

        if (delivery.getTrackingNo() != null
                && service.trackingNumberExists(
                        delivery.getTrackingNo().trim())) {
            result.rejectValue(
                    "trackingNo",
                    "duplicate",
                    "Tracking number already exists");
        }

        if (result.hasErrors()) {
            return "deliveries/form";
        }

        Delivery saved = service.create(delivery);

        String otpToUse = null;
        if (adminOtp != null && adminOtp.trim().matches("^\\d{6}$")) {
            otpToUse = adminOtp.trim();
        } else if (generateOtp) {
            otpToUse = otpService.generateSixDigitCode();
        }

        if (otpToUse != null) {
            OtpService.OtpGenerationResult otpResult = otpService.assignExplicitOtp(saved, otpToUse);
            redirectAttributes.addFlashAttribute("generatedOtp", otpResult.getPlainOtp());
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Delivery created! Generated OTP: " + otpResult.getPlainOtp()
            );
        }

        return "redirect:/deliveries/" + saved.getId();
    }


    @PostMapping("/{id}/generate-otp")
    public String generateOtpForExisting(
            @PathVariable Long id,
            @RequestHeader(value = "Referer", required = false) String referer,
            RedirectAttributes redirectAttributes) {

        try {
            Delivery delivery = service.findById(id);
            delivery.setOtpStatus(OtpStatus.NONE);
            delivery.setOtpExpiry(null);
            OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
            redirectAttributes.addFlashAttribute("generatedOtp", otpResult.getPlainOtp());
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Generated 6-digit OTP for " + delivery.getTrackingNo() + ": " + otpResult.getPlainOtp()
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        if (referer != null && referer.contains("/admin/workflow")) {
            return "redirect:/admin/workflow";
        }
        return "redirect:/deliveries/" + id;
    }



    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("delivery", service.findById(id));
        return "deliveries/detail";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("delivery", service.findById(id));
        model.addAttribute("deliveryId", id);
        return "deliveries/edit";
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("delivery") Delivery delivery,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("deliveryId", id);
            return "deliveries/edit";
        }

        try {
            service.updateDetails(id, delivery);
            return "redirect:/deliveries/" + id;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            result.reject("updateError", ex.getMessage());
            model.addAttribute("deliveryId", id);
            return "deliveries/edit";
        }
    }
}