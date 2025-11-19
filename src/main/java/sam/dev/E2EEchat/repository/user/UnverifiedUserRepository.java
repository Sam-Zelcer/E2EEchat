package sam.dev.E2EEchat.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sam.dev.E2EEchat.repository.entitys.user.UnverifiedUser;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Long> {

    void deleteUnverifiedUsersByExpirationBefore(LocalDateTime now);
    Optional<UnverifiedUser> findUnverifiedUserByCode(Integer code);
}
