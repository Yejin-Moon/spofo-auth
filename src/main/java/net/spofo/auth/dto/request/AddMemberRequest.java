package net.spofo.auth.dto.request;

import lombok.Getter;
import net.spofo.auth.entity.Member;

@Getter
public class AddMemberRequest {

    private String platform;
    private String socialId;

    public Member toEntity() {
        return Member.builder()
                .platform(platform)
                .socialId(socialId)
                .build();
    }
}
