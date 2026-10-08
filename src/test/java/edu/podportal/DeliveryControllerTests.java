package edu.podportal;

import edu.podportal.model.DeliveryStatus;
import edu.podportal.model.Delivery;
import edu.podportal.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void cleanDatabase() {
        deliveryRepository.deleteAll();
    }

    @Test
    void listPageLoads() throws Exception {
        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(view().name("deliveries/list"));
    }

    @Test
    void createDeliverySavesAndRedirects() throws Exception {
        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trackingNo", "TRK-001")
                        .param("customer", "Sahil")
                        .param("address", "Mumbai")
                        .param("item", "Laptop"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/deliveries/*"));

        Delivery delivery = deliveryRepository.findAll().get(0);

        org.junit.jupiter.api.Assertions.assertEquals(
                "TRK-001",
                delivery.getTrackingNo()
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                "CREATED",
                delivery.getStatus().name()
        );
    }

    @Test
    void missingFieldsShowValidationErrors() throws Exception {
        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trackingNo", "")
                        .param("customer", "")
                        .param("address", "")
                        .param("item", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("deliveries/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "delivery",
                        "trackingNo",
                        "customer",
                        "address",
                        "item"
                ));
    }

    @Test
    void duplicateTrackingNumberIsRejected() throws Exception {
        Delivery first = new Delivery();
        first.setTrackingNo("TRK-DUP");
        first.setCustomer("Customer 1");
        first.setAddress("Mumbai");
first.setItem("Phone");
first.setStatus(DeliveryStatus.CREATED);

deliveryRepository.save(first);

        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trackingNo", "TRK-DUP")
                        .param("customer", "Customer 2")
                        .param("address", "Pune")
                        .param("item", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(view().name("deliveries/form"))
                .andExpect(content().string(
                        containsString("Tracking number already exists")
                ));
    }
}
