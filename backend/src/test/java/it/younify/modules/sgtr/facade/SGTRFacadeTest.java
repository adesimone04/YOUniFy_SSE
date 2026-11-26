package it.younify.modules.sgtr.facade;

import it.younify.modules.sgtr.model.dto.request.CommentPostRequest;
import it.younify.modules.sgtr.model.dto.request.ThreadPostRequest;
import it.younify.modules.sgtr.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SGTRFacadeTest {

    @Mock private ThreadPostService threadPostService;
    @Mock private CommentPostService commentPostService;
    @Mock private CensorshipService censorshipService;

    // Mockiamo gli altri service usati dal facade per evitare NullPointerException nel costruttore
    @Mock private ThreadViewService threadViewService;
    @Mock private ThreadDeleteService threadDeleteService;
    @Mock private ThreadVoteService threadVoteService;
    @Mock private TotalCFUService totalCFUService;
    @Mock private CommentViewService commentViewService;
    @Mock private CommentDeleteService commentDeleteService;
    @Mock private CommentVoteService commentVoteService;

    @InjectMocks private ThreadFacade threadFacade;
    @InjectMocks private CommentFacade commentFacade;

    //TC3_SGTR (Censura Thread)
    @Test
    void createThread_TC3_ShouldCensorContent_WhenSensitive() {
        // ARRANGE
        String dirtyContent = "Sei veramente un *insulto* e non capisci nulla.";
        String cleanContent = "Sei veramente un SIG. ROSINA e non capisci nulla.";

        ThreadPostRequest request = new ThreadPostRequest();
        request.setContent(dirtyContent);
        request.setFlag("DIDATTICA");

        // Simuliamo che il servizio di censura trovi la parola e la pulisca
        when(censorshipService.containsSensitiveContent(dirtyContent)).thenReturn(true);
        when(censorshipService.censorText(dirtyContent)).thenReturn(cleanContent);

        threadFacade.createThread(request);

        // Verifichiamo che al Service di salvataggio sia arrivato il testo PULITO
        ArgumentCaptor<ThreadPostRequest> captor = ArgumentCaptor.forClass(ThreadPostRequest.class);
        verify(threadPostService).post(captor.capture());

        assertEquals(cleanContent, captor.getValue().getContent());
    }

    //TC7_SGTR (Censura Commento)
    @Test
    void createComment_TC7_ShouldCensorContent_WhenSensitive() {
        // ARRANGE
        String dirtyContent = "Sei un *insulto* a chiederlo qui.";
        String cleanContent = "Sei un SIG. ROSINA a chiederlo qui.";

        CommentPostRequest request = new CommentPostRequest();
        request.setContent(dirtyContent);
        request.setThreadId(1L);

        when(censorshipService.containsSensitiveContent(dirtyContent)).thenReturn(true);
        when(censorshipService.censorText(dirtyContent)).thenReturn(cleanContent);

        commentFacade.createComment(request);

        ArgumentCaptor<CommentPostRequest> captor = ArgumentCaptor.forClass(CommentPostRequest.class);
        verify(commentPostService).post(captor.capture());

        assertEquals(cleanContent, captor.getValue().getContent());
    }
}