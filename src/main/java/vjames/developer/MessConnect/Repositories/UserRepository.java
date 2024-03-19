package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vjames.developer.MessConnect.Entities.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Transactional
    @Query("SELECT U FROM User U WHERE U.username = :username ORDER BY U.id LIMIT 1")
    Optional<User> findByUsername(@Param("username") String username);
    @Query("SELECT u FROM User u WHERE u.id = :userId" )
    Optional<User> findById(@Param("userId") String userId);
    @Query("SELECT u FROM User u WHERE u.id = :userId" )
    Optional<User> findByIdOptional(String userId);
    @Modifying
    @Transactional
    @Query("UPDATE User U SET U.password = :newPassword WHERE U.id = :userId")
    void updatePassword(@Param("newPassword") String newPassword, @Param("userId") String userId);
    @Modifying
    @Transactional
    @Query("UPDATE User U SET U.status = :status WHERE U.username = :username")
    void updateStatus(String status, String username);
    @Transactional
    @Query("SELECT U FROM User U WHERE U.id != :userId AND U.status = :status")
    List<User> findAllByStatus(@Param("userId") String userId, @Param("status") String status);
//    select u.id, u.username from user U WHERE u.id != '123c4a53-aff6-487f-aab1-170101937ee8'
//    and u.id NOT IN (
//            select f.following_id from following f where f.user_id = '123c4a53-aff6-487f-aab1-170101937ee8'
//    ) order by rand() limit 10;
    @Transactional
    @Query("SELECT U FROM User U WHERE  U.id != :userId AND U.id NOT IN (SELECT F.followingId FROM Follows F WHERE F.userId = :userId)" +
            "ORDER BY Rand() LIMIT 10")
    List<User> findAllIfNotFollowingByCurrenUserId(@Param("userId") String userId);
}
