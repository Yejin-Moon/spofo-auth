package net.spofo.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "publickey")
public class PublicKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String publickey;

    @Column(length = 500, nullable = false)
    private String n;
    @Column(length = 20, nullable = false)
    private String e;



    @Builder
    public PublicKey(String publickey, String n, String e) {
        this.publickey = publickey;
        this.n=n;
        this.e=e;
    }

}
