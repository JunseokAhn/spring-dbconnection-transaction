package jdbc.example;

import java.sql.SQLException;
import java.util.List;

public interface Repository {
    Member save(Member member) throws SQLException;

    Member findById(Long id) throws SQLException;

    List<Member> findByName(String name) throws SQLException;

    boolean updateMoney(Long id, Long money) throws SQLException;

    int deleteById(Long id) throws SQLException;

    int deleteAll() throws SQLException;
}
