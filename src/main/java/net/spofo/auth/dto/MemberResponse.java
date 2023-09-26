package net.spofo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.spofo.auth.entity.Member;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private String socialId;
    private String platform;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .socialId(member.getSocialId())
                .platform(member.getPlatform())
                .build();
    }
}
