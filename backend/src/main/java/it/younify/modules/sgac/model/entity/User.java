package it.younify.modules.sgac.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * The {@code User} class is a JPA entity that represents a user account in the system.
 * It also implements the Spring Security {@link UserDetails} interface to integrate with the authentication mechanism.
 * <p>
 * This entity stores information about users, such as their name, email, university, and other personal details.
 */
@Entity
@Table(name = "users")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    private String ID;

    /**
     * The user's first name.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * The user's email address, which is unique and used as the username for authentication.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The file path or URL to the user's profile image.
     */
    private String profileImage;

    /**
     * The user's course of study (Corso di Laurea).
     */
    @Column(nullable = false)
    private String cdl;

    /**
     * The user's university.
     */
    @Column(nullable = false)
    private String university;

    /**
     * The user's date of birth.
     */
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    /**
     * The user's contact information.
     */
    @Column(nullable = false)
    private String contactsInfo;

    /**
     * A flag indicating whether the user's account is enabled.
     * Disabled accounts cannot be authenticated.
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * The user's role, which defines their permissions and defaults to USER.
     */
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;


    public User(String ID, String firstName, String lastName, String email, String password,
                String cdl, String university, LocalDate dateOfBirth, String contactsInfo, boolean enabled) {

        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.cdl = cdl;
        this.university = university;
        this.dateOfBirth = dateOfBirth;
        this.contactsInfo = contactsInfo;
        this.enabled = enabled;
    }



    /**
     * Returns the authorities granted to the user.
     * @return A collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return The user's email address.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return The user's hashed password.
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return {@code true} if the user is enabled, {@code false} otherwise.
     */
    @Override public boolean isEnabled() { return enabled; }
}
