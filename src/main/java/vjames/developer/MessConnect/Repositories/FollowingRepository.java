package vjames.developer.MessConnect.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vjames.developer.MessConnect.Entities.Follows;

import java.util.List;

public interface FollowingRepository extends JpaRepository<Follows, String> {
    @Transactional
    @Query("SELECT F FROM Follows F WHERE F.userId = :fromUser AND F.followingId = :toUser ORDER BY F.userId LIMIT  1")
    Follows findByUserIdAndFollowingId(@Param("fromUser") String fromUser,@Param("toUser") String toUser);

    @Transactional
    @Query("SELECT U.id as id, U.appUserName as appUserName, U.avatar as avartar, U.firstName as firstName, U" +
            ".lastName as lastName, F.followAt as followAt  FROM User U JOIN Follows F ON U.id = F.followingId " +
            "WHERE F.userId = :userId ORDER BY F.followAt DESC")
    List<Object[]> findAllUserFollowingFromUserId(String userId);

    @Transactional
    @Query("SELECT U.id as id, U.appUserName as appUserName, U.avatar as avartar, U.firstName as firstName, U." +
            "lastName as lastName, F.followAt as followAt FROM User U JOIN Follows  F ON U.id = F.userId WHERE " +
            "F.followingId = :userFollowed ORDER BY F.followAt DESC")
    List<Object[]> findAllFollowsToUser(@Param("userFollowed") String userFollowed);
}
