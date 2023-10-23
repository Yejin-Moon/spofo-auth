package net.spofo.auth.controller;

import static org.springframework.http.ResponseEntity.ok;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.dto.request.AddMemberRequest;
import net.spofo.auth.exception.SocialIdNotFound;
import net.spofo.auth.service.MemberService;
import net.spofo.auth.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final VerificationService verificationService;

    @GetMapping("/auth/members/search")
    public ResponseEntity<MemberResponse> verifyValidMember(
            @RequestHeader("authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        MemberResponse memberResponse = verificationService.verifyToken(token);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/auth/members")
    public ResponseEntity<MemberResponse> insertMember(@RequestBody AddMemberRequest request) {
        try {
            memberService.findBySocialId(request.getSocialId());
        } catch (SocialIdNotFound e) {
            MemberResponse savedMember = memberService.save(request);
            return ok().body(savedMember);
        }
        return ResponseEntity.noContent().build(); // 이미 있는 회원
    }
}
