package net.spofo.auth.repository;

import net.spofo.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findBySocialId(String socialId);
}
