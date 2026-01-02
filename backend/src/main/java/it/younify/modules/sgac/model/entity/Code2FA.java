package it.younify.modules.sgac.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * The {@code Code2FA} class is a JPA entity that represents a two-factor authentication (2FA) code.
 * <p>
 * This entity stores the 2FA code, its expiration date, and the number of incorrect attempts
 * for a specific user or host, identified by the {@code id} field.
 */
@Entity
@Table(name = "code_2FA")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Code2FA {

    /**
     * The unique identifier for the 2FA code, which corresponds to the ID of the user or host.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The 6-digit 2FA code.
     */
    @Column(nullable = false, length = 6)
    private String code;

    /**
     * The date and time when the 2FA code expires.
     */
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * The number of times an incorrect 2FA code has been entered.
     */
    @Column(nullable = false)
    private int errorCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", unique = true)
    private Host host;
}
