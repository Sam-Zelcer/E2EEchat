package sam.dev.E2EEchat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.auth.SignInRequest;
import sam.dev.E2EEchat.repository.dtos.auth.SignUpRequest;
import sam.dev.E2EEchat.service.user.AuthorizationService;

@RestController
@RequestMapping("/unauthorized")
@RequiredArgsConstructor
public class UnauthorizedController {

    private final AuthorizationService authorizationService;

    @PostMapping("/sign-up")
    public String signUp(
            @RequestBody @Valid
            SignUpRequest request
    ) {
        return authorizationService.singUp(request);
    }

    @GetMapping("/verification")
    public String verification(
            @RequestParam
            String code
    ) {
        return authorizationService.verification(Integer.parseInt(code));
    }

    @PostMapping("/sign-in")
    public String signIn(
            @RequestBody @Valid
            SignInRequest request
    ) {
        return authorizationService.signIn(request);
    }
}
