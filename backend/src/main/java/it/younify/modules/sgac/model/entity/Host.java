package it.younify.modules.sgac.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * The {@code Host} class is a JPA entity that represents a host account in the system.
 * It also implements the Spring Security {@link UserDetails} interface to integrate with the authentication mechanism.
 * <p>
 * This entity stores information about hosts, such as their name, VAT number (Partita IVA),
 * certified email address (PEC), and location.
 */
@Entity
@Table(name = "hosts")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Host implements UserDetails {

    /**
     * The unique identifier for the host.
     */
    @Id
    private String ID;

    /**
     * The name of the host.
     */
    @Column(nullable = false)
    private String hostName;

    /**
     * The host's VAT number (Partita IVA), which is unique.
     */
    @Column(nullable = false, unique = true)
    private String PIVA;

    /**
     * The file path or URL to the host's profile image.
     */
    private String profileImage;

    /**
     * The host's certified email address (PEC), which is unique and used as the username for authentication.
     */
    @Column(nullable = false, unique = true)
    private String PEC;

    /**
     * The host's hashed password.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The province where the host is located.
     */
    @Column(nullable = false)
    private String province;

    /**
     * The city where the host is located.
     */
    @Column(nullable = false)
    private String city;

    /**
     * A list of universities nearest the host.
     */
    @Column(nullable = false)
    private String uniList;

    /**
     * A flag indicating whether the host's account is enabled.
     * Disabled accounts cannot be authenticated.
     */
    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.HOST;

    public Host(String ID, String hostName, String PIVA, String PEC, String password, String province, String city, String uniList, boolean enabled) {

        this.ID = ID;
        this.hostName = hostName;
        this.PIVA = PIVA;
        this.PEC = PEC;
        this.password = password;
        this.province = province;
        this.city = city;
        this.uniList = uniList;
        this.enabled = enabled;
    }


    /**
     * Returns the authorities granted to the host.
     * In this case, it's always "ROLE_HOST".
     *
     * @return A collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Returns the username used to authenticate the host.
     *
     * @return The host's PEC.
     */
    @Override
    public String getUsername() {
        return PEC;
    }

    /**
     * Returns the password used to authenticate the host.
     *
     * @return The host's hashed password.
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