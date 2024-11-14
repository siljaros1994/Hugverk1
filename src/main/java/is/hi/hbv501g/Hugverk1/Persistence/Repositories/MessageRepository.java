package is.hi.hbv501g.Hugverk1.Persistence.Repositories;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByTimestamp(Long senderId, Long receiverId);
    List<Message> findByReceiverIdAndSenderIdOrderByTimestamp(Long receiverId, Long senderId);

    @Query("SELECT m FROM Message m WHERE (m.senderId = :senderId AND m.receiverId = :receiverId) OR (m.senderId = :receiverId AND m.receiverId = :senderId) ORDER BY m.timestamp")
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}