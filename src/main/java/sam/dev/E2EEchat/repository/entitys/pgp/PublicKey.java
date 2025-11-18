package sam.dev.E2EEchat.repository.entitys.pgp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sam.dev.E2EEchat.repository.entitys.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "public_key_table")
public class PublicKey {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "public_key_table_id_seq"
    )
    @SequenceGenerator(
            name = "public_key_table_id_seq",
            sequenceName = "public_key_table_id_seq",
            initialValue = 100000
    )
    private Long id;

    @Column(name = "public_key", nullable = false, unique = true, columnDefinition = "TEXT")
    private String publicKey;

    @ManyToOne()
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;
}
