package edu.podportal.controller;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.service.DeliveryService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WorkflowController {

    private final DeliveryService service;

    public WorkflowController(DeliveryService service) {
        this.service = service;
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

    @PostMapping("/agent/deliveries/{id}/proof")
    public String submitProof(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam String recipientName,
            @RequestParam String proofNote,
            RedirectAttributes redirectAttributes) {

        try {
            service.submitProof(
                    id,
                    authentication.getName(),
                    recipientName,
                    proofNote
            );
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Delivery proof submitted successfully"
            );
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