package sam.dev.E2EEchat.repository.pgp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.pgp.PublicKey;
import sam.dev.E2EEchat.repository.entitys.user.User;

import java.util.Optional;

@Repository
public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {

    @Query(
            "SELECT pk FROM PublicKey pk "+
                    "JOIN FETCH pk.owner " +
                    "WHERE "+
                    "pk.owner=:user"
    )
    Optional<PublicKey> findPublicKeyByOwner(
            @Param("user") User user
    );
}
