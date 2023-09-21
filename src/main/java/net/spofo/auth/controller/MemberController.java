package net.spofo.auth.controller;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    @Autowired
    MemberRepository memberRepository;

    @GetMapping("/auth/members/search")
    public Long searchMember(@RequestHeader HttpHeaders httpHeaders) {
        String searchedMember = httpHeaders.getFirst("authorization");
        Long id = memberRepository.findBySocialId(searchedMember).getId();
        return id;
    }
}
