package sam.dev.E2EEchat.repository.dtos.pgp.privateChat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePrivateChatRequest {

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9:]+$",
            message = "Username must contain only letters and numbers, and ':' which split username and users id"
    )
    @Size(min = 3, max = 100)
    private String secondUserNameId;
}
