package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.backup.CreateBackUpRequest;
import sam.dev.E2EEchat.service.pgp.backup.CreateBackupService;
import sam.dev.E2EEchat.service.pgp.backup.DeleteBackupService;

@RestController
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

    private final CreateBackupService createBackupService;
    private final DeleteBackupService deleteBackupService;

    @PostMapping("/create")
    public String createBackup(
            @RequestBody @Valid
            CreateBackUpRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return createBackupService.createBackup(request, authHeader);
    }

    @PostMapping("/delete")
    public String deleteBackup(
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return deleteBackupService.deleteBackup(authHeader);
    }
}
