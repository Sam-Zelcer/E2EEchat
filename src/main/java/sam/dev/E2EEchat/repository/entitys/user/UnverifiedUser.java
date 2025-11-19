package sam.dev.E2EEchat.repository.entitys.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unverified_user_table")
public class UnverifiedUser {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "unverified_user_table_id_seq"
    )
    @SequenceGenerator(
            name = "unverified_user_table_id_seq",
            sequenceName = "unverified_user_table_id_seq",
            initialValue = 100000
    )
    private Long id;

    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "Username must contain only letters and numbers"
    )
    @Column(name = "username", nullable = false, unique = true, length = 120)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiration;

    @Column(name = "code", nullable = false)
    private Integer code;
}
