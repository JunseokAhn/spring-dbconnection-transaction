package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcRepositoryTest {

//    Repository repository = new JdbcRepository(null); // DriverManager
//    Repository repository = new JdbcRepository(ConnectionUtil.getDriverDataSource()); // DriverDataSource
    Repository repository = new JdbcRepository(ConnectionUtil.getHikariDataSource()); // HikariDataSource - dbcp

    @AfterEach
    void destroy() throws SQLException {
        repository.deleteAll();
    }

    @Test
    void save() throws SQLException {
        Member member = new Member(1L, "1", 10L);
        repository.save(member);
        Member foundMember = repository.findById(member.getId());
        assertThat(member).isEqualTo(foundMember);
    }

    @Test
    void deleteById() throws SQLException {
        repository.save(new Member(1L, "1", 10L));
        repository.deleteById(1L);
        assertThatThrownBy(()->
                repository.findById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findByName() throws SQLException {
        Member member = new Member(1L, "1", 10L);
        Member member2 = new Member(2L, "1", 10L);
        repository.save(member);
        repository.save(member2);
        List<Member> members = repository.findByName("1");
        for (Member foundMember : members) {
            assertThat(foundMember.getName()).isEqualTo("1");
        }
    }

    @Test
    void addMoney() throws SQLException {
        Member member = new Member(1L, "1", 10L);
        repository.save(member);
        Long money = repository.findById(1L).getMoney();
        repository.updateMoney(member.getId(), money+10L);
        Member foundMember = repository.findById(1L);
        assertThat(foundMember.getMoney()).isEqualTo(20L);
    }

}