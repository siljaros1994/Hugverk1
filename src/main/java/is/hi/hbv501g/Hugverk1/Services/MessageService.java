package is.hi.hbv501g.Hugverk1.Services;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;
import is.hi.hbv501g.Hugverk1.Persistence.Repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getConversationBetween(Long userId1, Long userId2) {
        List<Message> conversation = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(userId1, userId2);
        System.out.println("Number of messages in conversation: " + conversation.size());

        for (Message message : conversation) {
            String direction = (message.getSenderId().equals(userId1)) ? "Sent" : "Received";
            System.out.println(direction + " Message: " + message.getContent() + " (Sender ID: " + message.getSenderId() + ", Receiver ID: " + message.getReceiverId() + ")");
        }

        conversation.sort(Comparator.comparing(Message::getTimestamp));
        return conversation;
    }

    public void reportUser(Long reporterId, Long reportedId) {
        // Placeholder for reporting functionality, need to connect to admin;
        System.out.println("User with ID " + reporterId + " reported user with ID " + reportedId);
    }
}