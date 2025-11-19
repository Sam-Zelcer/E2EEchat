package sam.dev.E2EEchat.repository.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Username must contain only letters and numbers"
    )
    @Size(min = 3, max = 80)
    private String username;

    @Email
    @Size(max = 150)
    private String email;

    @Size(min = 8, max = 100)
    private String password;
}
