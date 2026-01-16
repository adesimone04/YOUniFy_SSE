package it.younify.modules.sgn.service;


import it.younify.modules.sgac.model.entity.User;
import it.younify.modules.sgac.repository.UserRepository;
import it.younify.modules.sgn.model.entity.Type;
import it.younify.modules.sgn.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NotificationDeleteService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteMatchNotify(User receiverUser){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User senderUser = (User) principal;
        notificationRepository.deleteAllBySenderUserAndReceiverUserAndType(senderUser,receiverUser,Type.MATCH);
    }
}
