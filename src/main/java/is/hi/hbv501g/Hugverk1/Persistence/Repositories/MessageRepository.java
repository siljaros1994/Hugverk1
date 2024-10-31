package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndRecipientIdOrderByTimestamp(String senderId, String recipientId);
    List<Message> findByRecipientIdAndSenderIdOrderByTimestamp(String recipientId, String senderId);
}