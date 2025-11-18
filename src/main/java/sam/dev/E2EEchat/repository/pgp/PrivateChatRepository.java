package sam.dev.E2EEchat.repository.pgp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {

    @Query(
            "SELECT pch FROM PrivateChat pch " +
                    "JOIN FETCH pch.firstUser " +
                    "JOIN FETCH pch.secondUser " +
                    "WHERE " +
                    "(pch.firstUser = :firstUser AND pch.secondUser = :secondUser) " +
                    "OR " +
                    "(pch.firstUser = :secondUser AND pch.secondUser = :firstUser)"
    )
    Optional<PrivateChat> findExistingPrivateChat(
            @Param("firstUser") User firstUser,
            @Param("secondUser") User secondUser
    );

    @Query(
            "SELECT pch FROM PrivateChat pch "+
                    "JOIN FETCH pch.firstUser " +
                    "JOIN FETCH pch.secondUser "+
                    "WHERE pch.firstUser=:user OR pch.secondUser=:user"
    )
    Optional<List<PrivateChat>> findUsersPrivateChats(
            @Param("user") User user
    );
}
