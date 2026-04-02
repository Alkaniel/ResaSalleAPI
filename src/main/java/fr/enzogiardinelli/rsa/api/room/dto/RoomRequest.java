package fr.enzogiardinelli.rsa.api.room.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoomRequest {

    @NotBlank(message = "Le nom de la salle est obligatoire")
    @Size(max = 100, message = "Le nom de la salle ne doit pas dépasser 100 caractères")
    private String name;

    @Min(value = 1, message = "La capacité doit être d'au moins 1 place")
    private int capacity;

    private List<String> equipments;
}
