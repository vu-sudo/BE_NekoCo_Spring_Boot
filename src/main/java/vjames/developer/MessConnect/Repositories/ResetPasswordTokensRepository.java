package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import vjames.developer.MessConnect.Entities.ResetPasswordTokens;


public interface ResetPasswordTokensRepository extends JpaRepository<ResetPasswordTokens, String> {
    @Transactional
    ResetPasswordTokens findByUserId(String userId);
    @Transactional
    ResetPasswordTokens findByResetCode(String resetCode);
    @Modifying
    @Transactional
    void deleteById(String id);

}
