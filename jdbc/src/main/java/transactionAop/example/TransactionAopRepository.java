package transactionAop.example;

import jdbc.example.Member;
import jdbc.example.Repository;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * txManager 도입으로 모든 메서드가 동일하게 동작함
 */
public class TransactionAopRepository implements Repository {
    private final DataSource dataSource;

    public TransactionAopRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(id, name, money) values (?,?,?)";
        Connection connection = getConnection(dataSource);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, member.getId());
        pstmt.setString(2, member.getName());
        pstmt.setLong(3, member.getMoney());
        pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return member;
    }

    @Override
    public Member findById(Long id) throws SQLException {
        String sql = "select * from member where id = ?";
        Connection connection = getConnection(dataSource);
        ResultSet rs = null;
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, id);
        rs = pstmt.executeQuery();
        Member member = null;
        if (rs.next() != false) {
            member = Member.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .money(rs.getLong("money"))
                    .build();
        } else {
            throw new NoSuchElementException();
        }
        closeConnection(connection, pstmt, rs);
        return member;
    }

    @Override
    public int deleteById(Long id) throws SQLException {
        String sql = "delete member where id = ?";
        Connection connection = getConnection(dataSource);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, id);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result;
    }

    @Override
    public int deleteAll() throws SQLException {
        String sql = "delete member";
        Connection connection = getConnection(dataSource);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result;
    }

    @Override
    public List<Member> findByName(String name) throws SQLException {
        String sql = "select * from member where name = ?";
        Connection connection = getConnection(dataSource);
        ResultSet rs = null;
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        rs = pstmt.executeQuery();
        List<Member> members = new LinkedList<>();
        Member member = null;
        while (rs.next() != false) {
            member = Member.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .money(rs.getLong("money"))
                    .build();
            members.add(member);
        }
        closeConnection(connection, pstmt, rs);
        return members;
    }

    /**
     * 멤버의 money를 입력받은 money로 대체한다
     *
     * @param id
     * @param money
     * @return true / false
     * @throws SQLException
     */
    @Override
    public boolean updateMoney(Long id, Long money) throws SQLException {
        String sql = "update member set money = ? where id = ?";
        Connection connection = getConnection(dataSource);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, money);
        pstmt.setLong(2, id);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result == 1 ? true : false;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }


    /**
     * 트랜잭션 동기화 매니저에서 관리중이면 보관된 커넥션을 꺼내오고,
     * 트랜잭션 동기화 매니저에서 관리중이 아니면 새 커넥션을 생성해 반환한다.
     */
    private Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
        try {
            return DataSourceUtils.doGetConnection(dataSource);
        } catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        } catch (IllegalStateException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    /**
     * 트랜잭션 동기화 매니저에서 관리중이면 txManager에 커넥션을 반환하고,
     * 트랜잭션 동기화 매니저에서 관리중이 아니면 커넥션을 종료한다
     */
    private void closeConnection(Connection connection, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            JdbcUtils.closeResultSet(rs);
        }
        if (pstmt != null) {
            JdbcUtils.closeStatement(pstmt);
        }
        if (connection != null) {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
