package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class BaseInitDataTest {
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("샘플 회원 6명이 생성되어 있다")
    void t1() {
        assertThat(memberService.count()).isEqualTo(6);

        String[][] expected = {
                {"system", "시스템"},
                {"admin", "관리자"},
                {"user1", "유저1"},
                {"user2", "유저2"},
                {"user3", "유저3"},
                {"user4", "유저4"}
        };

        for (String[] row : expected) {
            Member member = memberService.findByUsername(row[0]).get();

            assertThat(member.getName()).isEqualTo(row[1]);
            assertThat(member.getPassword()).isEqualTo("12345678");
        }
    }
}
