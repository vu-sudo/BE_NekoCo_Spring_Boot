package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vjames.developer.MessConnect.Entities.ChatRoomOneOnOne;

import java.util.Optional;

public interface ChatRoom1On1Repository extends JpaRepository<ChatRoomOneOnOne, String> {


    Optional<ChatRoomOneOnOne> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
