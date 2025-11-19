package sam.dev.E2EEchat.repository.pgp;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.pgp.Message;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(
            "SELECT m FROM Message m " +
                    "JOIN FETCH m.sender " +
                    "JOIN FETCH m.privateChat " +
                    "WHERE m.privateChat = :privateChat " +
                    "ORDER BY m.at DESC"
    )
    Optional<List<Message>> findMessagesByPrivateChat(
            @Param("privateChat") PrivateChat privateChat,
            Pageable pageable
    );

    @Query(
            "SELECT m FROM Message m " +
                    "JOIN FETCH m.sender " +
                    "JOIN FETCH m.privateChat " +
                    "WHERE m.privateChat = :privateChat AND m.id < :lastMessageId "+
                    "ORDER BY m.at DESC"
    )
    Optional<List<Message>> findExtraMessagesByPrivateChat(
            @Param("privateChat") PrivateChat privateChat,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );
}
