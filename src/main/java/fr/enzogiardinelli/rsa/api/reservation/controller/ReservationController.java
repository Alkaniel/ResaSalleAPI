package fr.enzogiardinelli.rsa.api.reservation.controller;


import fr.enzogiardinelli.rsa.api.reservation.dto.ReservationRequest;
import fr.enzogiardinelli.rsa.api.reservation.model.Reservation;
import fr.enzogiardinelli.rsa.api.reservation.model.ReservationStatus;
import fr.enzogiardinelli.rsa.api.reservation.repository.ReservationRepository;
import fr.enzogiardinelli.rsa.api.reservation.service.ReservationService;
import fr.enzogiardinelli.rsa.api.user.model.User;
import fr.enzogiardinelli.rsa.api.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    /**
     * Créer une réservation.
     * L'ID de l'utilisateur est extrait de façon sécurisée depuis le JWT.
     */
    @PostMapping
    public ResponseEntity<?> bookRoom(@Valid @RequestBody ReservationRequest request, Authentication authentication) {

        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));

        try {
            Reservation reservation = reservationService.createReservation(
                    user.getId(),
                    request.getRoomId(),
                    request.getStartTime(),
                    request.getEndTime()
            );
            return ResponseEntity.ok("Réservation confirmée avec l'ID : " + reservation.getId());

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Récupérer toutes les réservations de l'utilisateur connecté (Mon Espace).
     */
    @GetMapping("/me")
    public ResponseEntity<List<Reservation>> getMyReservations(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Reservation> myReservations = reservationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(myReservations);
    }

    /**
     * Annuler une réservation.
     * Un USER ne peut annuler QUE ses propres réservations.
     * Un MANAGER peut annuler n'importe laquelle.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable UUID id, Authentication authentication) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        String userEmail = authentication.getName();
        boolean isManager = authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER")) ||
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("SUPER_ADMIN"));

        if (!reservation.getUser().getEmail().equals(userEmail) && !isManager) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Erreur : Vous n'avez pas le droit d'annuler cette réservation.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return ResponseEntity.ok("La réservation a été annulée avec succès.");
    }
}
