package vjames.developer.MessConnect.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vjames.developer.MessConnect.Entities.ChatMessage;
import vjames.developer.MessConnect.Entities.ChatNotification;
import vjames.developer.MessConnect.Services.ChatMessageService;
import vjames.developer.MessConnect.Utils.ApplicationResponseData;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/private-message")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ) {
        ChatMessage saveMessage = chatMessageService.saveMessage(chatMessage);
        ChatNotification chatNotification =  ChatNotification.builder()
                .id(saveMessage.getId())
                .senderId(saveMessage.getSenderId())
                .recipientId(saveMessage.getRecipientId())
                .content(saveMessage.getContent())
                .createAt(saveMessage.getCreateAt())
                .build();
        //john/queue/message
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/private", chatNotification
        );
        System.out.println(chatNotification);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> findChatMessages(
            @RequestParam("senderId") String senderId,
            @RequestParam("recipientId") String recipientId
    ) {
        return ApplicationResponseData.buildResponse(HttpStatus.OK,
                "",
                chatMessageService.findChatMessages(senderId, recipientId));
    }

}
