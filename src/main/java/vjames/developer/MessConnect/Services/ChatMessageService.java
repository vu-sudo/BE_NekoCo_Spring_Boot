package vjames.developer.MessConnect.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vjames.developer.MessConnect.Entities.ChatMessage;
import vjames.developer.MessConnect.Repositories.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomOneOnOneService chatRoomOneOnOneService;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        var chatId = chatRoomOneOnOneService.getChatId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true
        ).orElseThrow(); //create your own dedicated exception
        var chatRoomPrivateId = chatRoomOneOnOneService.getChatRoomPrivateId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId()
        ).orElseThrow();
        chatMessage.setChatRoomId(chatRoomPrivateId);
        chatMessage.setChatId(chatId);
        chatMessage.setCreateAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }
    public List<ChatMessage> findChatMessages(
            String senderId,
            String recipientId
    ) {
        var chatId = chatRoomOneOnOneService.getChatId(
                senderId,
                recipientId,
                false);
        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }
}
