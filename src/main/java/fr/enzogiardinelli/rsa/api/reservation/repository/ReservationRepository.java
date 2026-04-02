package fr.enzogiardinelli.rsa.api.reservation.repository;

import fr.enzogiardinelli.rsa.api.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUserId(UUID userId);
    List<Reservation> findByRoomId(UUID roomId);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.room.id = :roomId " +
        "AND r.status = 'CONFIRMED' " +
        "AND (r.startTime < :endTime AND r.endTime > :startTime)")
    boolean existsConflictingReservation(@Param("roomId") UUID roomId,
                                         @Param("startTime")LocalDateTime startTime,
                                         @Param("endTime")LocalDateTime endTime);
}
