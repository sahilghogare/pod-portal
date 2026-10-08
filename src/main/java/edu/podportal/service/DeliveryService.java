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
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));
    }

    public boolean trackingNumberExists(String trackingNo) {
        return repository.existsByTrackingNo(trackingNo);
    }

    public Delivery create(Delivery delivery) {
        delivery.setId(null);
        delivery.setStatus(DeliveryStatus.CREATED);
        return repository.save(delivery);
    }
}