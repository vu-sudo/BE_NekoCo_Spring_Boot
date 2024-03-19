package vjames.developer.MessConnect.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.ChatRoomOneOnOne;
import vjames.developer.MessConnect.Repositories.ChatRoom1On1Repository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomOneOnOneService {
    private final ChatRoom1On1Repository chatRoom1On1Repository;
    public Optional<String> getChatId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        return chatRoom1On1Repository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoomOneOnOne::getChatId)
                .or(
                () -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                }
        );
    }
    public Optional<String> getChatRoomPrivateId (
            String senderId,
            String recipientId
    ) {
        return chatRoom1On1Repository.findBySenderIdAndRecipientId(senderId, recipientId).map(ChatRoomOneOnOne::getId);
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoomOneOnOne senderRecipient = ChatRoomOneOnOne.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoomOneOnOne recipientSender = ChatRoomOneOnOne.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoom1On1Repository.save(senderRecipient);
        chatRoom1On1Repository.save(recipientSender);
        return chatId;
    }
}
