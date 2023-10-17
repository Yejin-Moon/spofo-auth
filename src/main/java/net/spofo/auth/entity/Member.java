package net.spofo.auth.entity;

import lombok.Getter;
import lombok.Builder;
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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String platform;

    @Column(length = 500, nullable = false)
    private String socialId;

    @Builder
    private Member(Long id, String platform, String socialId) {
        this.id = id;
        this.platform = platform;
        this.socialId = socialId;
    }

    public static Member from(Long id, String platform, String socialId) {
        return Member.builder()
                .id(id)
                .platform(platform)
                .socialId(socialId)
                .build();
    }
}
