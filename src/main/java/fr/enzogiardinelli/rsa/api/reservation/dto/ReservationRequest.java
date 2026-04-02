package fr.enzogiardinelli.rsa.api.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationRequest {

    @NotNull(message = "L'ID de la salle est obligatoire")
    private UUID roomId;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La réservation doit être dans le futur")
    private LocalDateTime startTime;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La réservation doit être dans le futur")
    private LocalDateTime endTime;
}
