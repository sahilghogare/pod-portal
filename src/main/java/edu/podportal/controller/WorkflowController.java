package edu.podportal.controller;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.model.OtpStatus;
import edu.podportal.service.DeliveryService;
import edu.podportal.service.OtpService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WorkflowController {

    private final DeliveryService service;
    private final OtpService otpService;

    public WorkflowController(DeliveryService service, OtpService otpService) {
        this.service = service;
        this.otpService = otpService;
    }


    // ADMIN: View deliveries and assign an agent
    @GetMapping("/admin/workflow")
    public String adminWorkflow(Model model) {
        model.addAttribute("deliveries", service.findAll());
        return "workflow/admin";
    }

    @PostMapping("/admin/deliveries/{id}/assign")
    public String assignDelivery(
            @PathVariable Long id,
            @RequestParam String agentUsername,
            RedirectAttributes redirectAttributes) {

        try {
            service.assignToAgent(id, agentUsername);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Delivery assigned to " + agentUsername
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage()
            );
        }

        return "redirect:/admin/workflow";
    }

    // AGENT: View only deliveries assigned to the logged-in agent
    @GetMapping("/agent/deliveries")
    public String agentDeliveries(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "deliveries",
                service.findAll().stream()
                        .filter(delivery ->
                                authentication.getName().equals(
                                        delivery.getAssignedAgent()))
                        .toList()
        );

        return "workflow/agent";
    }

    @PostMapping("/agent/deliveries/{id}/out-for-delivery")
    public String markOutForDelivery(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            service.markOutForDelivery(id, authentication.getName());
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Delivery marked Out for Delivery"
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage()
            );
        }

        return "redirect:/agent/deliveries";
    }

    @PostMapping("/agent/deliveries/{id}/verify-otp")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String otp,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();
        try {
            OtpService.OtpVerificationResult result =
                    otpService.verifyOtp(id, authentication.getName(), otp.trim());

            response.put("status", result.getStatus().name());
            response.put("message", result.getMessage());
            response.put("attemptsLeft", result.getAttemptsLeft());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            response.put("status", "ERROR");
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/agent/deliveries/{id}/proof")
    public String submitProof(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam String recipientName,
            @RequestParam String proofNote,
            @RequestParam(required = false) String otp,
            RedirectAttributes redirectAttributes) {

        try {
            Delivery delivery = service.findById(id);

            if (Boolean.TRUE.equals(delivery.getOtpRequired())
                    && delivery.getOtpStatus() != OtpStatus.VERIFIED
                    && otp != null
                    && !otp.isBlank()) {
                otpService.verifyOtp(id, authentication.getName(), otp.trim());
            }

            service.submitProof(
                    id,
                    authentication.getName(),
                    recipientName,
                    proofNote
            );

            Delivery updated = service.findById(id);
            if (Boolean.TRUE.equals(updated.getOtpRequired())
                    && updated.getOtpStatus() == OtpStatus.VERIFIED) {
                // Auto-verify delivery for reviewer
                service.verifyProof(id);
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Delivery proof submitted and automatically verified via matching OTP!"
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "successMessage", "Delivery proof submitted successfully"
                );
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage()
            );
        }

        return "redirect:/agent/deliveries";
    }



    // REVIEWER: View deliveries and review submitted proof
    @GetMapping("/reviewer/deliveries")
    public String reviewerDeliveries(Model model) {
        model.addAttribute("deliveries", service.findAll());
        return "workflow/reviewer";
    }

    @PostMapping("/reviewer/deliveries/{id}/verify")
    public String verifyProof(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            service.verifyProof(id);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Delivery proof verified"
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage()
            );
        }

        return "redirect:/reviewer/deliveries";
    }

    @PostMapping("/reviewer/deliveries/{id}/reject")
    public String rejectProof(
            @PathVariable Long id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {

        try {
            service.rejectProof(id, reason);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Delivery proof rejected"
            );
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage()
            );
        }

        return "redirect:/reviewer/deliveries";
    }
}