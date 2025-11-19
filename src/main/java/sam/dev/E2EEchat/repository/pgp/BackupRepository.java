package sam.dev.E2EEchat.repository.pgp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.pgp.Backup;
import sam.dev.E2EEchat.repository.entitys.user.User;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

    void deleteAllByOwner(User user);
}
