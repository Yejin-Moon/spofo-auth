package net.spofo.auth.controller;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.dto.MemberResponse;
import net.spofo.auth.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
