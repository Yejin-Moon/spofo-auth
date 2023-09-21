package net.spofo.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "Member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String platform;

    @Column(length = 500, nullable = false)
    private String socialId;

    @Builder
    public Member(String platform, String socialId) {
        this.platform = platform;
        this.socialId = socialId;
    }
}
