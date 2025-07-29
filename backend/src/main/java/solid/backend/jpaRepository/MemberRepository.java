package solid.backend.jpaRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solid.backend.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    /**
     * 설명: 아이디 찾기 이메일 확인
     * @param email
     */
    Optional<Member> findByMemberEmail(String email);
}