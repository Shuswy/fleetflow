package com.fleetflow.order_service.repository;

import com.fleetflow.order_service.model.MechanicRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MechanicRequestRepository extends JpaRepository<MechanicRequest, Long> {
    List<MechanicRequest> findAllByMechanicId(Long mechanicId);
    Optional<MechanicRequest> findByIdAndMechanicId(Long id, Long mechanicId);
}
