package sam.dev.E2EEchat.repository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomEntityRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomEntityRepository.class);

    @PersistenceContext
    private final EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void createIndexes() {
        try {
            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST hash_idx_user_username ON user_table USING HASH (username)"
            ).executeUpdate();

            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST idx_unverified_user_expiration ON unverified_user_table (username)"
            ).executeUpdate();

            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST hash_idx_public_key_owner ON public_key_table USING HASH (owner_id)"
            ).executeUpdate();

            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST hash_idx_private_chat_first_user ON private_chat_table USING HASH (first_user)"
            ).executeUpdate();

            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST hash_idx_private_chat_second_user ON private_chat_table USING HASH (second_user)"
            ).executeUpdate();

            entityManager.createNativeQuery(
                    "CREATE INDEX IF NOT EXIST hash_idx_private_chat_id ON message_table USING HASH (chat_id)"
            ).executeUpdate();

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }
}
