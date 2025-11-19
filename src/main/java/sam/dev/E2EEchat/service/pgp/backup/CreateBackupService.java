package sam.dev.E2EEchat.service.pgp.backup;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.backup.CreateBackUpRequest;
import sam.dev.E2EEchat.repository.entitys.pgp.Backup;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.BackupRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class CreateBackupService {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateBackupService.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BackupRepository backupRepository;

    public String createBackup(CreateBackUpRequest request, String authHeader) {
        try {
            if (
                    authHeader == null || authHeader.length() < 7 ||
                            request.getEncryptedPrivateKeys() == null ||
                            request.getEncryptedPrivateKeys().isEmpty()
            ) return "bad request";

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return "bad jwt token";
            if (optionalUser.isEmpty()) return "your account no longer exists";

            backupRepository.deleteAllByOwner(optionalUser.get());
            List<Backup> backups = request.getEncryptedPrivateKeys().stream()
                    .map(
                            key -> new Backup(
                                    null,
                                    optionalUser.get(),
                                    key
                            )
                    ).toList();
            backupRepository.saveAll(backups);
            return "backup was create";

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "something went wrong";
    }
}
