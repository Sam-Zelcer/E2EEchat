package sam.dev.E2EEchat.configuration.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sam.dev.E2EEchat.service.jwt.JWTService;
import sam.dev.E2EEchat.service.user.MyUserDetailsService;

@Component
@RequiredArgsConstructor
public class WebsocketJWTInterceptor implements ChannelInterceptor {

    private final JWTService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                try {
                    token = token.substring(7);
                    UserDetails userDetails = myUserDetailsService.loadUserById(jwtService.extractUsersId(token));

                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        accessor.setUser(authenticationToken);
                    }

                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return message;
    }
}
