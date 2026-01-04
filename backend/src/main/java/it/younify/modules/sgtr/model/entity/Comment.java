package it.younify.modules.sgtr.model.entity;

import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "comment")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private int CFU;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Instant publicationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id")
    private Threads threadId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;
}
