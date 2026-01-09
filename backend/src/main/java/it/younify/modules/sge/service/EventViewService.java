package it.younify.modules.sge.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventViewService {
    private final ImageStorageService imageStorageService;
    private final EventRepository eventRepository;

    @SneakyThrows
    public Page<EventResponse> getEventsbyDate(Pageable pageable) {
        return eventRepository.findAllByOrderByDateandtimeAsc(pageable).map(this::toResponse);
    }

    @SneakyThrows
    public Page<EventResponse> getEventsbyHost(Host host, Pageable pageable){
        return eventRepository.findAllByHostOrderByDateandtimeAsc(host, pageable).map(this::toResponse);
    }

    @SneakyThrows
    public Page<EventResponse> getEventsbyCity(String city, Pageable pageable){
        return eventRepository.findAllByCityOrderByDateandtimeAsc(city, pageable).map(this::toResponse);
    }

    @SneakyThrows
    public Page<EventResponse> getEventsbyJoined(User user, Pageable pageable){
        return eventRepository.findJoinedEventsByUser(user, pageable).map(this::toResponse);
    }

    private EventResponse toResponse(Event event){
        try {
            return EventResponse.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .dateandtime(event.getDateandtime())
                    .street(event.getStreet())
                    .city(event.getCity())
                    .CAP(event.getCAP())
                    .posterUrl(imageStorageService.getPresignedUrl(event.getPoster()))
                    .hostName(event.getHost().getHostName())
                    .build();
        }catch (IOException e){
            return null;
        }

    }
}
