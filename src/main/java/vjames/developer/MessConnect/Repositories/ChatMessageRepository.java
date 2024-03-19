package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vjames.developer.MessConnect.Entities.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Transactional
    @Query("SELECT M FROM ChatMessage M WHERE M.chatId = :s ORDER BY M.createAt")
    List<ChatMessage> findByChatId(@Param("s") String s);
}
