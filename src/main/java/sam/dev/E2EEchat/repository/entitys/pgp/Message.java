package sam.dev.E2EEchat.repository.entitys.pgp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sam.dev.E2EEchat.repository.entitys.user.User;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_table")
public class Message {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "message_table_id_seq"
    )
    @SequenceGenerator(
            name = "message_table_id_seq",
            sequenceName = "message_table_id_seq",
            initialValue = 100000
    )
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @ManyToOne()
    @JoinColumn(name = "chat_id", nullable = false)
    private PrivateChat privateChat;

    @Column(name = "encrypted_session_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedSessionKey;

    @Column(name = "encrypted_message", nullable = false, length = 1000)
    private String encryptedMessage;

    @Column(name = "at", nullable = false)
    private String at;
}
