package fr.enzogiardinelli.rsa.api.room.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @ElementCollection
    @CollectionTable(
            name = "room_equipments",
            joinColumns = @JoinColumn(name = "room_id")
    )
    private List<String> equipments = new ArrayList<>();

    @Column(name = "is_available")
    @Builder.Default
    private boolean isAvailable = true;
}
