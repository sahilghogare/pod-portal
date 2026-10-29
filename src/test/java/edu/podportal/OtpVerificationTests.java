package edu.podportal;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.model.OtpStatus;
import edu.podportal.repository.DeliveryRepository;
import edu.podportal.service.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OtpVerificationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OtpService otpService;

    @BeforeEach
    void cleanDatabase() {
        deliveryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanGenerateOtpWhenCreatingDelivery() throws Exception {
        MvcResult result = mockMvc.perform(post("/deliveries").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trackingNo", "TRK-OTP-01")
                        .param("customer", "Alice Smith")
                        .param("address", "456 Elm Street")
                        .param("item", "Electronics")
                        .param("generateOtp", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("generatedOtp"))
                .andReturn();

        String generatedOtp = (String) result.getFlashMap().get("generatedOtp");
        assertNotNull(generatedOtp);
        assertTrue(generatedOtp.matches("^\\d{6}$"), "OTP must be exactly 6 digits");

        Delivery saved = deliveryRepository.findAll().get(0);
        assertTrue(saved.getOtpRequired());
        assertEquals(OtpStatus.PENDING, saved.getOtpStatus());
        assertNotNull(saved.getOtpHashed());
        assertNotEquals(generatedOtp, saved.getOtpHashed(), "OTP must be stored as a hash, not plain text");
        assertNotNull(saved.getOtpExpiry());
        assertTrue(saved.getOtpExpiry().isAfter(LocalDateTime.now()));
    }

    @Test
    void otpContainsExactlySixDigits() {
        for (int i = 0; i < 20; i++) {
            String otp = otpService.generateSixDigitCode();
            assertNotNull(otp);
            assertEquals(6, otp.length());
            assertTrue(otp.matches("^\\d{6}$"));
        }
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void agentCanSubmitOtpForAuthorizedDeliveryAndCorrectOtpProducesVerified() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-02");
        delivery.setCustomer("Bob Jones");
        delivery.setAddress("789 Pine Ave");
        delivery.setItem("Documents");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
        String plainOtp = otpResult.getPlainOtp();

        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", plainOtp)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"))
                .andExpect(jsonPath("$.message").value(containsString("Successfully")));

        Delivery verified = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertEquals(OtpStatus.VERIFIED, verified.getOtpStatus());
        assertEquals(plainOtp, verified.getEnteredOtp());
        assertNotNull(verified.getOtpVerifiedAt());
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void incorrectOtpProducesIncorrect() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-03");
        delivery.setCustomer("Charlie");
        delivery.setAddress("101 Oak Blvd");
        delivery.setItem("Camera");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        otpService.generateAndAssignOtp(delivery);

        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", "000000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INCORRECT"))
                .andExpect(jsonPath("$.message").value("Incorrect OTP"));

        Delivery updated = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertEquals(OtpStatus.INCORRECT, updated.getOtpStatus());
        assertEquals("000000", updated.getEnteredOtp());
        assertEquals(1, updated.getOtpAttempts());
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void expiredOtpCannotBeAccepted() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-04");
        delivery.setCustomer("David");
        delivery.setAddress("202 Cedar St");
        delivery.setItem("Watch");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
        delivery.setOtpExpiry(LocalDateTime.now().minusMinutes(2)); // Expired
        deliveryRepository.save(delivery);

        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", otpResult.getPlainOtp())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"))
                .andExpect(jsonPath("$.message").value(containsString("expired")));

        Delivery updated = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertEquals(OtpStatus.EXPIRED, updated.getOtpStatus());
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void repeatedFailedAttemptsAreLimited() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-05");
        delivery.setCustomer("Eve");
        delivery.setAddress("303 Maple Rd");
        delivery.setItem("Shoes");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        otpService.generateAndAssignOtp(delivery);

        // 5 failed attempts
        for (int i = 1; i <= 5; i++) {
            mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                            .param("otp", "11111" + i)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("INCORRECT"));
        }

        // 6th attempt should fail due to max attempts exceeded
        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", "999999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INCORRECT"))
                .andExpect(jsonPath("$.message").value(containsString("Maximum verification attempts exceeded")));
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void otpCannotBeReusedAfterSuccessfulVerification() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-06");
        delivery.setCustomer("Frank");
        delivery.setAddress("404 Birch Way");
        delivery.setItem("Tablet");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
        String plainOtp = otpResult.getPlainOtp();

        // First verification succeeds
        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", plainOtp)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERIFIED"));

        // Second verification should not re-verify or change state
        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", plainOtp)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("cannot be reused")));
    }

    @Test
    @WithMockUser(username = "agent2", roles = "AGENT")
    void agentCannotSubmitOtpForUnauthorizedDelivery() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-07");
        delivery.setCustomer("Grace");
        delivery.setAddress("505 Walnut Ln");
        delivery.setItem("Headphones");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent1"); // assigned to agent1, not agent2
        deliveryRepository.save(delivery);

        otpService.generateAndAssignOtp(delivery);

        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/verify-otp").with(csrf())
                        .param("otp", "123456")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("own assigned deliveries")));
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void agentCannotAccessOriginalOtpInPage() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-08");
        delivery.setCustomer("Henry");
        delivery.setAddress("606 Willow Ct");
        delivery.setItem("Keyboard");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
        String plainOtp = otpResult.getPlainOtp();

        MvcResult result = mockMvc.perform(get("/agent/deliveries"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains(plainOtp), "Agent page must NEVER leak the plain OTP");
        assertFalse(content.contains(delivery.getOtpHashed()), "Agent page must not leak the OTP hash");
    }

    @Test
    @WithMockUser(username = "reviewer", roles = "REVIEWER")
    void reviewerCanSeeSubmittedOtpAndVerificationResultButCannotRetrieveOriginalOtp() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-OTP-09");
        delivery.setCustomer("Irene");
        delivery.setAddress("707 Spruce St");
        delivery.setItem("Book");
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setAssignedAgent("agent");
        delivery.setRecipientName("Irene");
        delivery.setProofNote("Delivered with verified OTP");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);
        String plainOtp = otpResult.getPlainOtp();

        // Simulate agent verified with OTP
        delivery.setEnteredOtp(plainOtp);
        delivery.setOtpStatus(OtpStatus.VERIFIED);
        delivery.setOtpVerifiedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        MvcResult result = mockMvc.perform(get("/reviewer/deliveries"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OTP Verified Successfully")))
                .andExpect(content().string(containsString(plainOtp))) // The submitted OTP entered by agent
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(content.contains(delivery.getOtpHashed()), "Reviewer page must not display the OTP hash");
    }

    @Test
    @WithMockUser(username = "reviewer", roles = "REVIEWER")
    void reviewerCanStillManuallyApproveOrRejectDelivery() throws Exception {
        // Test manual approval
        Delivery delivery1 = new Delivery();
        delivery1.setTrackingNo("TRK-OTP-10");
        delivery1.setCustomer("Jack");
        delivery1.setAddress("808 Ash St");
        delivery1.setItem("Monitor");
        delivery1.setStatus(DeliveryStatus.DELIVERED);
        delivery1.setAssignedAgent("agent");
        delivery1.setRecipientName("Jack");
        delivery1.setProofNote("Delivered");
        delivery1.setOtpStatus(OtpStatus.INCORRECT); // Even if incorrect, manual decision is preserved
        deliveryRepository.save(delivery1);

        mockMvc.perform(post("/reviewer/deliveries/" + delivery1.getId() + "/verify").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviewer/deliveries"));

        Delivery verified = deliveryRepository.findById(delivery1.getId()).orElseThrow();
        assertEquals(DeliveryStatus.VERIFIED, verified.getStatus(), "Reviewer manually approved delivery");

        // Test manual rejection
        Delivery delivery2 = new Delivery();
        delivery2.setTrackingNo("TRK-OTP-11");
        delivery2.setCustomer("Kelly");
        delivery2.setAddress("909 Beech St");
        delivery2.setItem("Desk");
        delivery2.setStatus(DeliveryStatus.DELIVERED);
        delivery2.setAssignedAgent("agent");
        delivery2.setRecipientName("Kelly");
        delivery2.setProofNote("Left at door");
        delivery2.setOtpStatus(OtpStatus.INCORRECT);
        deliveryRepository.save(delivery2);

        mockMvc.perform(post("/reviewer/deliveries/" + delivery2.getId() + "/reject").with(csrf())
                        .param("reason", "OTP was incorrect and signature missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviewer/deliveries"));

        Delivery rejected = deliveryRepository.findById(delivery2.getId()).orElseThrow();
        assertEquals(DeliveryStatus.REJECTED, rejected.getStatus(), "Reviewer manually rejected delivery");
        assertEquals("OTP was incorrect and signature missing", rejected.getRejectionReason());
    }

    @Test
    @WithMockUser(username = "agent", roles = "AGENT")
    void agentSubmittingCorrectOtpAutoVerifiesDeliveryForReviewer() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNo("TRK-AUTO-01");
        delivery.setCustomer("Auto Customer");
        delivery.setAddress("Auto Address");
        delivery.setItem("Auto Package");
        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        delivery.setAssignedAgent("agent");
        deliveryRepository.save(delivery);

        OtpService.OtpGenerationResult otpResult = otpService.generateAndAssignOtp(delivery);

        mockMvc.perform(post("/agent/deliveries/" + delivery.getId() + "/proof").with(csrf())
                        .param("recipientName", "Auto Recipient")
                        .param("proofNote", "Delivered with matching OTP")
                        .param("otp", otpResult.getPlainOtp()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agent/deliveries"));

        Delivery verified = deliveryRepository.findById(delivery.getId()).orElseThrow();
        assertEquals(DeliveryStatus.VERIFIED, verified.getStatus(), "Delivery should be auto-verified for Reviewer upon matching OTP!");
        assertEquals(OtpStatus.VERIFIED, verified.getOtpStatus());
    }
}

