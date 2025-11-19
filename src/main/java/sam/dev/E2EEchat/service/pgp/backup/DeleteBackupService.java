package sam.dev.E2EEchat.service.pgp.backup;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.BackupRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class DeleteBackupService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteBackupService.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BackupRepository backupRepository;

    public String deleteBackup(String authHeader) {
        try {
            if (authHeader.isEmpty()) return "bad request";

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return "bad jwt token";
            if (optionalUser.isEmpty()) return "your account no longer exists";


            backupRepository.deleteAllByOwner(optionalUser.get());
            return "backup was deleted";

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "something went wrong";
    }
}
