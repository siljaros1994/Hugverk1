package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestamp(String senderId, String receiverId);
    List<Message> findByReceiverIdAndSenderIdOrderByTimestamp(String receiverId, String senderId);
}