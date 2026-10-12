package edu.podportal.service;

import edu.podportal.model.Delivery;
import edu.podportal.model.DeliveryStatus;
import edu.podportal.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Delivery markOutForDelivery(Long id, String username) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.ASSIGNED);
        requireAssignedAgent(delivery, username);

        delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);

        return repository.save(delivery);
    }

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

    public Delivery verifyProof(Long id) {
        Delivery delivery = findById(id);

        requireStatus(delivery, DeliveryStatus.DELIVERED);

        delivery.setStatus(DeliveryStatus.VERIFIED);
        delivery.setRejectionReason(null);

        return repository.save(delivery);
    }

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