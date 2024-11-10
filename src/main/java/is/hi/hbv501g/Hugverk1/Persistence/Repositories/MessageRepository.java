package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestamp(Long senderId, Long receiverId);
    List<Message> findByReceiverIdAndSenderIdOrderByTimestamp(Long receiverId, Long senderId);

    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) OR (m.senderId = :userId2 AND m.receiverId = :userId1) ORDER BY m.timestamp")
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}