package it.younify.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "hosts")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Host implements UserDetails {

    @Id
    private String ID;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false, unique = true)
    private String PIVA;

    private String profileImage;

    @Column(nullable = false, unique = true)
    private String PEC;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String uniList;

    public Host(String ID, String hostName, String PIVA, String PEC, String password, String province, String city, String uniList){

        this.ID = ID;
        this.hostName = hostName;
        this.PIVA = PIVA;
        this.PEC = PEC;
        this.password = password;
        this.province = province;
        this.city = city;
        this.uniList = uniList;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_HOST"));
    }

    @Override
    public String getUsername() {
        return PEC;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

}