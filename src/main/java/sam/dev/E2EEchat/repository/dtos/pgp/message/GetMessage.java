package sam.dev.E2EEchat.repository.dtos.pgp.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetMessage {

    private String sender;
    private String encryptedSessionKey;
    private String encryptedMessage;
    private String at;

}
