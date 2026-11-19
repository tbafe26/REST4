package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member join(String username, String password, String name) {
        memberRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new MemberDuplicateUsernameException(username);
                });

        Member member = new Member(username, password, name);

        return memberRepository.save(member);
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }

    // actorId 로 행위자를 찾는다. 없거나(0 포함) 존재하지 않으면 401
    public Member getActor(int actorId) {
        return findById(actorId).orElseThrow(
                UnauthenticatedException::new
        );
    }

    public Optional<Member> findLatest() {
        return memberRepository.findTopByOrderByIdDesc();
    }
}
