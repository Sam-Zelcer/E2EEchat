package sam.dev.E2EEchat.repository.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Username must contain only letters and numbers"
    )
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
