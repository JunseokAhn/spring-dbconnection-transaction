package com.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class JdbcServiceTest {

    private final JdbcService service= new JdbcService();
    @AfterEach
//    @Test
    void destroy() throws SQLException {
        service.deleteAll();
    }
    @Test
    @DisplayName("생성된 멤버와 찾은 멤버가 같다")
    void saveMember() throws SQLException {
        Member newMember = service.saveMember(new Member("UserA", 1000L));
        Member foundMember = service.findMemberById(0L);
        Assertions.assertThat(newMember).isEqualTo(foundMember);
    }

    @Test
    @DisplayName("멤버1은 500, 멤버2는 1500의 money를 갖고있다.")
    void accountTransfer() throws SQLException {
        Member member1 = service.saveMember(new Member("UserA", 1000L));
        Member member2 = service.saveMember(new Member("UserB", 1000L));
        service.accountTransfer(member1.getId(), member2.getId(), 500L);
        Assertions.assertThat(service.findMemberById(member1.getId()).getMoney()).isEqualTo(500L);
        Assertions.assertThat(service.findMemberById(member2.getId()).getMoney()).isEqualTo(1500L);
    }

}