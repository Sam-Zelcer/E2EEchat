package sam.dev.E2EEchat.repository.pgp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.pgp.PublicKey;

@Repository
public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {
}
