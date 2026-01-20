package it.younify.modules.sgm.model.entity;

import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matching")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
