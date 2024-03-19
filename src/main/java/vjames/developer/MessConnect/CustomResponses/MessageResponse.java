package vjames.developer.MessConnect.CustomResponses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class MessageResponse {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
    private LocalDateTime createAt;
}
