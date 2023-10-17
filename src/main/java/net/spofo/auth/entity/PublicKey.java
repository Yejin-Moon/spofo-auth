package net.spofo.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String publickey;

    @Column(length = 500, nullable = false)
    private String modulus;

    @Column(length = 20, nullable = false)
    private String exponent;

    @Builder
    private PublicKey(String publicKey, String modulus, String exponent) {
        this.publickey = publicKey;
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public static PublicKey from(String publicKey, String modulus, String exponent) {
        return PublicKey.builder()
                .publicKey(publicKey)
                .modulus(modulus)
                .exponent(exponent)
                .build();
    }
}