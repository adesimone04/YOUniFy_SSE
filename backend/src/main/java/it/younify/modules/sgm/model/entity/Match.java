package it.younify.modules.sgm.model.entity;

import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a match entity in the database, tracking interactions between two users.
 * This class maps to the `matching` table and stores information about match requests and their status.
 */
@Entity
@Table(name = "matching")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    /**
     * The unique identifier for the match.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    /**
     * The user who initiated the match request.
     * @see User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    /**
     * The user who received the match request.
     * @see User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    /**
     * The current status of the match (e.g., PENDING, ACCEPTED, REJECTED).
     * @see Status
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
