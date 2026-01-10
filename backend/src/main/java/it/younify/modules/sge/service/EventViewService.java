package it.younify.modules.sge.service;

import it.younify.core.service.ImageStorageService;
import it.younify.modules.sgac.model.entity.Host;
import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sge.model.dto.response.EventResponse;
import it.younify.modules.sge.model.dto.response.JoinedUserResponse;
import it.younify.modules.sge.model.entity.Event;
import it.younify.modules.sge.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventViewService {

    private final ImageStorageService imageStorageService;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByDate(Pageable pageable){

        return eventRepository.findAllByOrderByDateAndTimeAsc(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByHost(Host host, Pageable pageable){

        return eventRepository.findAllByHostOrderByDateAndTimeAsc(host, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByCity(String city, Pageable pageable){

        return eventRepository.findAllByCityOrderByDateAndTimeAsc(city, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByJoined(User user, Pageable pageable){

        return eventRepository.findDistinctByParticipationsUser(user, pageable).map(this::toResponse);
    }


    private EventResponse toResponse(Event event){

        List<JoinedUserResponse> joinedUsers = event.getParticipations().stream()
                        .map(participation -> {

                        User user = participation.getUser();
                        return JoinedUserResponse.builder()
                                .fullName(user.getFirstName() + " " + user.getLastName())
                                .profileImageUrl(imageStorageService.getPresignedUrl(user.getProfileImage()))
                                .build();

                    })
                    .toList();


            return EventResponse.builder()
                    .ID(event.getID())
                    .name(event.getName())
                    .description(event.getDescription())
                    .dateAndTime(event.getDateAndTime())
                    .street(event.getStreet())
                    .city(event.getCity())
                    .CAP(event.getCAP())
                    .posterUrl(imageStorageService.getPresignedUrl(event.getPoster()))
                    .hostName(event.getHost().getHostName())
                    .joinedUsers(joinedUsers)
                    .build();
    }
}
