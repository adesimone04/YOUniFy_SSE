package it.younify.modules.sgtr.service;

import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgtr.model.dto.request.ThreadVoteRequest;
import it.younify.modules.sgtr.model.entity.Threads;
import it.younify.modules.sgtr.model.entity.VoteType;
import it.younify.modules.sgtr.repository.ThreadRepository;
import it.younify.modules.sgtr.repository.ThreadVoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SGTRServiceTest {

    @Mock private ThreadRepository threadRepository;
    @Mock private ThreadVoteRepository threadVoteRepository;
    @Mock private SecurityContext securityContext;

    @InjectMocks private ThreadPostService threadPostService;
    @InjectMocks private ThreadVoteService threadVoteService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder().ID("u1").build();
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(user, null));
    }

    // TC11_SGTR: Coerenza Upvote (Calcolo CFU)
    @Test
    void voteThread_TC11_VerifyCFUCalculationConsistency_Up() {

        Threads thread = Threads.builder().ID(1L).CFU(5).build();
        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(VoteType.UPVOTE);

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));
        // Simuliamo che l'utente non abbia ancora votato
        when(threadVoteRepository.findByThreadAndUser(thread, user)).thenReturn(Optional.empty());


        threadVoteService.vote(request);

        assertEquals(6, thread.getCFU());
        verify(threadRepository).save(thread);
    }


    // TC12_SGTR: Coerenza CFU Upvote
    @Test
    void voteThread_TC12_CFU_Consistency_Up() {

        Threads thread = Threads.builder().ID(1L).CFU(8).build();
        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(VoteType.UPVOTE);

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));
        when(threadVoteRepository.findByThreadAndUser(thread, user)).thenReturn(Optional.empty());

        threadVoteService.vote(request);

        assertEquals(9, thread.getCFU());
        verify(threadRepository).save(thread);
    }

    // TC15_SGTR: Coerenza Downvote (Calcolo CFU)
    @Test
    void voteThread_TC15_VerifyCFUCalculation_Down() {

        Threads thread = Threads.builder().ID(1L).CFU(10).build();

        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(VoteType.DOWNVOTE);

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));
        // Simuliamo nuovo voto
        when(threadVoteRepository.findByThreadAndUser(thread, user)).thenReturn(Optional.empty());

        threadVoteService.vote(request);

        // Verifica logica: 10 - 1 = 9
        assertEquals(9, thread.getCFU());
        verify(threadRepository).save(thread);
    }


    // TC16_SGTR: Coerenza CFU Downvote
    @Test
    void voteThread_TC16_CFU_Consistency_Down() {
        // ARRANGE
        // Stato iniziale: 10 Up - 2 Down = 8 CFU
        Threads thread = Threads.builder().ID(1L).CFU(8).build();

        ThreadVoteRequest request = new ThreadVoteRequest();
        request.setThreadId(1L);
        request.setVoteType(VoteType.DOWNVOTE); // Azione: Downvote

        when(threadRepository.findById(1L)).thenReturn(Optional.of(thread));
        when(threadVoteRepository.findByThreadAndUser(thread, user)).thenReturn(Optional.empty()); // Nuovo voto

        // ACT
        threadVoteService.vote(request);

        assertEquals(7, thread.getCFU());
        verify(threadRepository).save(thread);
    }
}