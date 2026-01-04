package it.younify.modules.sgtr.model.entity;


import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "threads")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Threads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ID;

    @Column(nullable = false)
    private int CFU;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String flag;

    @Column(nullable = false)
    private Instant publicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host IDHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User IDUser;

}
