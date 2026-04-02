package fr.enzogiardinelli.rsa.api.reservation.service;

import fr.enzogiardinelli.rsa.api.reservation.model.Reservation;
import fr.enzogiardinelli.rsa.api.reservation.model.ReservationStatus;
import fr.enzogiardinelli.rsa.api.reservation.repository.ReservationRepository;
import fr.enzogiardinelli.rsa.api.room.model.Room;
import fr.enzogiardinelli.rsa.api.room.repository.RoomRepository;
import fr.enzogiardinelli.rsa.api.user.model.User;
import fr.enzogiardinelli.rsa.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Reservation createReservation(UUID userId, UUID roomId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de début doit être dans le futur.");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }

        Room room = roomRepository.findByIdWithLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Salle introuvable."));

        if (!room.isAvailable()) {
            throw new IllegalStateException("Cette salle est actuellement indisponible.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));

        boolean hasConflict = reservationRepository.existsConflictingReservation(roomId, startTime, endTime);
        if (hasConflict) {
            throw new IllegalStateException("Conflict de réservation : la salle est déjà occupée sur ce créneau.");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .startTime(startTime)
                .endTime(endTime)
                .status(ReservationStatus.CONFIRMED)
                .build();

        return reservationRepository.save(reservation);
    }
}
