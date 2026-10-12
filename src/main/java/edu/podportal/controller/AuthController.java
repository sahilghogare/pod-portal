package edu.podportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        model.addAttribute("loginError", error != null);
        model.addAttribute("loggedOut", logout != null);

        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {

        if (hasRole(authentication, "ROLE_ADMIN")) {
            return "redirect:/admin/home";
        }

        if (hasRole(authentication, "ROLE_AGENT")) {
            return "redirect:/agent/home";
        }

        if (hasRole(authentication, "ROLE_REVIEWER")) {
            return "redirect:/reviewer/home";
        }

        return "redirect:/login";
    }

    @GetMapping("/admin/home")
    public String adminHome(Authentication authentication, Model model) {
        populateHome(model, authentication, "ADMIN",
                "Administrator", "Admin Home",
                "Manage delivery records and create new deliveries.");
        return "role-home";
    }

    @GetMapping("/agent/home")
    public String agentHome(Authentication authentication, Model model) {
        populateHome(model, authentication, "AGENT",
                "Delivery Agent", "Agent Home",
                "Your assigned-delivery workspace.");
        return "role-home";
    }

    @GetMapping("/reviewer/home")
    public String reviewerHome(Authentication authentication, Model model) {
        populateHome(model, authentication, "REVIEWER",
                "Proof Reviewer", "Reviewer Home",
                "Your delivery-proof review workspace.");
        return "role-home";
    }

    private void populateHome(
            Model model,
            Authentication authentication,
            String roleCode,
            String roleLabel,
            String roleTitle,
            String description) {

        model.addAttribute("username", authentication.getName());
        model.addAttribute("roleCode", roleCode);
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("roleTitle", roleTitle);
        model.addAttribute("description", description);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals(role));
    }
}