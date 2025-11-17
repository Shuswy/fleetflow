package com.fleetflow.order_service.repository;

import com.fleetflow.order_service.model.MechanicRequest;
import com.fleetflow.order_service.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MechanicRequestRepository extends JpaRepository<MechanicRequest, Long> {
    List<MechanicRequest> findAllByMechanicId(Long mechanicId);
    Optional<MechanicRequest> findByIdAndMechanicId(Long id, Long mechanicId);
    List<MechanicRequest> findAllByStatus(RequestStatus status);
    @Query("SELECT DISTINCT mr FROM MechanicRequest mr JOIN mr.lineItems li WHERE li.status = :status")
    List<MechanicRequest> findAllWithLineItemStatus(@Param("status") RequestStatus status);
}
