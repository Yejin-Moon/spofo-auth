package net.spofo.auth.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String publicKey;

    @Column(length = 500, nullable = false)
    private String modulus;

    @Column(length = 20, nullable = false)
    private String exponent;

    @Builder
    public PublicKey(String publicKey, String modulus, String exponent) {
        this.publicKey = publicKey;
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