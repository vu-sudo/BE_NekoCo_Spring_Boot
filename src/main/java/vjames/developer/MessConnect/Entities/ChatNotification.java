package vjames.developer.MessConnect.Entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatNotification {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createAt;
}
