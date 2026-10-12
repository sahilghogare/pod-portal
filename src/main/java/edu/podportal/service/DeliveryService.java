package edu.podportal.service;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DeliveryService {

    private final DeliveryRepository repository;

    public DeliveryService(DeliveryRepository repository) {
        this.repository = repository;
    }

    public List<Delivery> findAll() {
        return repository.findAllByOrderByIdDesc();
    }

    public Delivery findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Delivery not found"));
    }

    public boolean trackingNumberExists(String trackingNo) {
        return repository.existsByTrackingNo(trackingNo);
    }

    public Delivery create(Delivery delivery) {
        delivery.setId(null);
        delivery.setStatus(DeliveryStatus.CREATED);
        return repository.save(delivery);
    }

    // Admin assigns a created delivery to an agent
    public Delivery assignToAgent(Long id, String agentUsername) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.CREATED);

        if (agentUsername == null || agentUsername.isBlank()) {
            throw new IllegalArgumentException("Agent is required");
        }

        delivery.setAssignedAgent(agentUsername.trim());
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        return repository.save(delivery);
    }

    // Agent marks their assigned delivery as out for delivery
    public Delivery markOutForDelivery(Long id, String username) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.ASSIGNED);
        requireAssignedAgent(delivery, username);

        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

        return repository.save(delivery);
    }

    // Agent submits recipient details and delivery proof
    public Delivery submitProof(
            Long id,
            String username,
            String recipientName,
            String proofNote) {

        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.OUT_FOR_DELIVERY);
        requireAssignedAgent(delivery, username);

        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient name is required");
        }

        if (proofNote == null || proofNote.isBlank()) {
            throw new IllegalArgumentException(
                    "Delivery proof note or OTP is required");
        }

        delivery.setRecipientName(recipientName.trim());
        delivery.setProofNote(proofNote.trim());
        delivery.setStatus(DeliveryStatus.DELIVERED);

        return repository.save(delivery);
    }

    // Reviewer verifies submitted proof
    public Delivery verifyProof(Long id) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.DELIVERED);

        delivery.setStatus(DeliveryStatus.VERIFIED);
        delivery.setRejectionReason(null);

        return repository.save(delivery);
    }

    // Reviewer rejects proof and records a reason
    public Delivery rejectProof(Long id, String reason) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.DELIVERED);

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required");
        }

        delivery.setRejectionReason(reason.trim());
        delivery.setStatus(DeliveryStatus.REJECTED);

        return repository.save(delivery);
    }

    // Search by tracking number, customer, and status
    public List<Delivery> search(
            String trackingNo,
            String customer,
            String status) {

        String tracking = normalize(trackingNo);
        String customerName = normalize(customer);
        String statusName = normalize(status);

        return findAll().stream()
                .filter(delivery ->
                        tracking.isEmpty()
                                || normalize(delivery.getTrackingNo())
                                        .contains(tracking))
                .filter(delivery ->
                        customerName.isEmpty()
                                || normalize(delivery.getCustomer())
                                        .contains(customerName))
                .filter(delivery ->
                        statusName.isEmpty()
                                || (delivery.getStatus() != null
                                && delivery.getStatus().name()
                                        .equalsIgnoreCase(statusName)))
                .toList();
    }

    // Update delivery details before delivery is completed
    public Delivery updateDetails(Long id, Delivery changes) {
        Delivery existing = findById(id);

        if (existing.getStatus() == DeliveryStatus.DELIVERED
                || existing.getStatus() == DeliveryStatus.VERIFIED
                || existing.getStatus() == DeliveryStatus.REJECTED) {
            throw new IllegalStateException(
                    "Delivery details cannot be updated after delivery");
        }

        if (changes.getTrackingNo() == null
                || changes.getCustomer() == null
                || changes.getAddress() == null
                || changes.getItem() == null) {
            throw new IllegalArgumentException(
                    "All delivery fields are required");
        }

        String trackingNo = changes.getTrackingNo().trim();
        String customer = changes.getCustomer().trim();
        String address = changes.getAddress().trim();
        String item = changes.getItem().trim();

        if (trackingNo.isEmpty()
                || customer.isEmpty()
                || address.isEmpty()
                || item.isEmpty()) {
            throw new IllegalArgumentException(
                    "All delivery fields are required");
        }

        if (!trackingNo.equals(existing.getTrackingNo())
                && repository.existsByTrackingNo(trackingNo)) {
            throw new IllegalArgumentException(
                    "Tracking number already exists");
        }

        existing.setTrackingNo(trackingNo);
        existing.setCustomer(customer);
        existing.setAddress(address);
        existing.setItem(item);

        return repository.save(existing);
    }

    // Count deliveries by status for the summary dashboard
    public Map<String, Long> statusCounts() {
        List<Delivery> deliveries = findAll();
        Map<String, Long> counts = new LinkedHashMap<>();

        for (DeliveryStatus status : DeliveryStatus.values()) {
            long count = deliveries.stream()
                    .filter(delivery -> delivery.getStatus() == status)
                    .count();

            counts.put(status.name(), count);
        }

        return counts;
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase(Locale.ROOT);
    }

    private void requireStatus(
            Delivery delivery,
            DeliveryStatus expectedStatus) {

        if (delivery.getStatus() != expectedStatus) {
            throw new IllegalStateException(
                    "Invalid status transition from "
                            + delivery.getStatus()
                            + ". Expected status: "
                            + expectedStatus);
        }
    }

    private void requireAssignedAgent(
            Delivery delivery,
            String username) {

        if (username == null
                || !username.equals(delivery.getAssignedAgent())) {
            throw new IllegalStateException(
                    "You can only manage your own assigned deliveries");
        }
    }
}