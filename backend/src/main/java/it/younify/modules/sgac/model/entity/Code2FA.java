package it.younify.modules.sgac.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "code_2FA")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Code2FA {

    @Id
    private String id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private int errorCount;
}
