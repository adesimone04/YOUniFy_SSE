package it.younify.modules.sgac.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * The {@code RefreshToken} class is a JPA entity that represents a refresh token.
 * <p>
 * This entity stores the refresh token, its expiration date, and its revocation status.
 * It is associated with either a {@link User} or a {@link Host}.
 */
@Entity
@Table(name = "refresh_token")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    /**
     * The unique identifier for the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    /**
     * The hashed value of the refresh token, which is stored in the database for security.
     */
    @Column(nullable = false, unique = true)
    private String tokenHashed;

    /**
     * A flag indicating whether the refresh token has been revoked.
     * Revoked tokens cannot be used to obtain new access tokens.
     */
    @Column(nullable = false)
    private boolean revoked;

    /**
     * The date and time when the refresh token expires.
     */
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * The user associated with this refresh token.
     * This is a many-to-one relationship, and it's lazily fetched.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The host associated with this refresh token.
     * This is a many-to-one relationship, and it's lazily fetched.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;
}
