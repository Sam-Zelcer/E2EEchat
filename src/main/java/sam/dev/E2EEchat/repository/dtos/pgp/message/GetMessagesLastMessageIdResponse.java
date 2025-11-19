package sam.dev.E2EEchat.repository.dtos.pgp.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetMessagesLastMessageIdResponse {

    private List<GetMessage> messages;
    private Long lastMessageId;
}
