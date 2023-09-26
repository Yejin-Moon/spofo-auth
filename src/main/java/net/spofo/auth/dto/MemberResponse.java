package net.spofo.auth.dto;

import lombok.Builder;
import lombok.Data;
import net.spofo.auth.entity.Member;

@Data
public class MemberResponse {

    private Long id;
    private String socialId;
    private String platform;

    @Builder
    private MemberResponse(Long id, String socialId, String platform) {
        this.id = id;
        this.socialId = socialId;
        this.platform = platform;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .socialId(member.getSocialId())
                .platform(member.getPlatform())
                .build();
    }
}