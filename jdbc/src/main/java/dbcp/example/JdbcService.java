package dbcp.example;

import jdbc.example.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static jdbc.example.ConnectionUtil.getConnection;


public class JdbcService {

    private final JdbcRepository repository;
    private Long memberId = 0L;

    public JdbcService(DataSource dataSource) {
        this.repository = new JdbcRepository(dataSource);
    }

    public Member saveMember(Member member) throws SQLException {
        member.setId(memberId++);
        return repository.save(member);
    }

    public Member findMemberById(Long memberId) throws SQLException {
        return repository.findById(memberId);
    }

    /**
     * from멤버에서 momey만큼 돈을 빼고, to멤버에서 money만큼 돈을 추가한다.
     * @param fromId from멤버 id
     * @param toId   to멤버 id
     * @param money
     * @throws SQLException
     */
    public void accountTransfer(Long fromId, Long toId, Long money) throws SQLException {
        Connection connection = getConnection(repository.getDataSource());
        try {
            connection.setAutoCommit(false);
            Member fromMember = repository.findById(fromId);
            Member toMember = repository.findById(toId);
            fromMember.setMoney(fromMember.getMoney() - money);
            toMember.setMoney(toMember.getMoney() + money);
            repository.updateMoney(fromId, fromMember.getMoney(), connection);
            repository.updateMoney(toId, toMember.getMoney(), connection);
            connection.commit();
//            throw new Exception("트랜잭션 테스트옹 Exception");
        } catch (Exception e) {
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            connection.setAutoCommit(true);
            connection.close();
        }

    }

    /**
     * 멤버 테이블을 전체 delete한다. 테스트용 메서드
     * @throws SQLException
     */
    public void deleteAll() throws SQLException {
        repository.deleteAll();
    }
}
