package net.spofo.auth.controller;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.response.MemberResponse;
import net.spofo.auth.dto.request.AddMemberRequest;
import net.spofo.auth.exception.NoSocialIdException;
import net.spofo.auth.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/auth/members/search")
    public ResponseEntity<MemberResponse> searchMember(
            @RequestHeader(value = "authorization") String searchedId) {
        MemberResponse memberResponse = memberService.findBySocialId(searchedId);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/auth/members")
    public ResponseEntity<MemberResponse> insertMember(@RequestBody AddMemberRequest request) {
        try {
            memberService.findBySocialId(request.getSocialId());
        } catch (NoSocialIdException e) {
            MemberResponse savedMember = memberService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMember);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); // 이미 있는 회원
    }

    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    String getStatus() {
        return "저는 인증서버예요.";
    }

    @GetMapping("/test/callStock")
    @ResponseStatus(HttpStatus.OK)
    String getStock() {
        return memberService.getStock();
    }

    @GetMapping("/test/callPortfolio")
    @ResponseStatus(HttpStatus.OK)
    String getPortfolio() {
        return memberService.getPortfolio();
    }
}
