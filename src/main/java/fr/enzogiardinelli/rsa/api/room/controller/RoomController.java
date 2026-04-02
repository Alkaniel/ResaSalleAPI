package fr.enzogiardinelli.rsa.api.room.controller;

import fr.enzogiardinelli.rsa.api.room.dto.RoomRequest;
import fr.enzogiardinelli.rsa.api.room.model.Room;
import fr.enzogiardinelli.rsa.api.room.repository.RoomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    /**
     * Accessible par tous les utilisateurs authentifiés.
     * Permet de récupérer uniquement les salles qui ne sont pas en maintenance.
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAvailableRooms() {
        List<Room> rooms = roomRepository.findByIsAvailableTrue();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Uniquement accessible par un profil MANAGER ou SUPER_ADMIN.
     * Permet d'ajouter une nouvelle salle au campus.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomRequest request) {
        Room newRoom = Room.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .equipments(request.getEquipments())
                .isAvailable(true)
                .build();

        roomRepository.save(newRoom);

        return ResponseEntity.ok("La salle " + newRoom.getName() + " a été créée avec succès !");
    }
}
