package net.spofo.auth.service;

import lombok.RequiredArgsConstructor;
import net.spofo.auth.entity.Member;
import net.spofo.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
}
