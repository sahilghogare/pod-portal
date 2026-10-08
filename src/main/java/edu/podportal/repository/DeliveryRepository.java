package edu.podportal.repository;

import edu.podportal.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    boolean existsByTrackingNo(String trackingNo);

    List<Delivery> findAllByOrderByIdDesc();
}