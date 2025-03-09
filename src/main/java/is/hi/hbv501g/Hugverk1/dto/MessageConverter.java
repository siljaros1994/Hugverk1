package is.hi.hbv501g.Hugverk1.dto;

import is.hi.hbv501g.Hugverk1.Persistence.Entities.Message;

public class MessageConverter {
    public static MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
