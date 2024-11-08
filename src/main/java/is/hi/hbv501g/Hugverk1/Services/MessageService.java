package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getConversation(Long senderId, Long receiverId) {
        List<Message> sentMessages = messageRepository.findBySenderIdAndReceiverIdOrderByTimestamp(senderId, receiverId);
        List<Message> receivedMessages = messageRepository.findByReceiverIdAndSenderIdOrderByTimestamp(receiverId, senderId);
        sentMessages.addAll(receivedMessages);
        sentMessages.sort(Comparator.comparing(Message::getTimestamp));
        return sentMessages;
    }
}