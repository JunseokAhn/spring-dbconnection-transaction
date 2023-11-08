package dbcp.example;

import jdbc.example.Member;
import jdbc.example.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static jdbc.example.ConnectionUtil.*;

public class JdbcRepository implements Repository {

    private final DataSource dataSource;
    public JdbcRepository(DataSource dataSource){
        this.dataSource= dataSource;
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

    /**
     * 멤버의 money를 입력받은 money로 대체한다
     * @param id
     * @param money
     * @param connection 트랜잭션 유지를 위한 추가 파라미터
     * @return
     * @throws SQLException
     */
    public boolean updateMoney(Long id, Long money, Connection connection) throws SQLException {
        String sql = "update member set money = ? where id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, money);
        pstmt.setLong(2, id);
        int result = pstmt.executeUpdate();
        closeConnection(null, pstmt, null); // connection을 닫지않고 트랜잭션을 유지한다
        return result == 1 ? true : false;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }

    public void closeConnection(Connection connection, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
