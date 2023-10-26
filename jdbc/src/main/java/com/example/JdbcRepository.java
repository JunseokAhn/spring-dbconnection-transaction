package com.example;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.example.ConnectionUtil.*;

public class JdbcRepository implements Repository {

    @Override
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(id, name, money) values (?,?,?)";
        Connection connection = getConnection();
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
        Connection connection = getConnection();
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
        Connection connection = getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, id);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result;
    }

    @Override
    public int deleteAll() throws SQLException {
        String sql = "delete member";
        Connection connection = getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result;
    }

    @Override
    public List<Member> findByName(String name) throws SQLException {
        String sql = "select * from member where name = ?";
        Connection connection = getConnection();
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

    @Override
    public boolean addMoney(Long id, Long money) throws SQLException {
        String sql = "update member set money = money+? where id = ?";
        Connection connection = getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setLong(1, money);
        pstmt.setLong(2, id);
        int result = pstmt.executeUpdate();
        closeConnection(connection, pstmt, null);
        return result == 1 ? true : false;
    }

}
