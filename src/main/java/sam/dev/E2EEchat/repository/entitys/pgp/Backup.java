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
@Table(name = "backup_table")
public class Backup {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "backup_table_id_seq"
    )
    @SequenceGenerator(
            name = "backup_table_id_seq",
            sequenceName = "backup_table_id_seq",
            initialValue = 100000
    )
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "encrypted_private_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedPrivateKey;
}
