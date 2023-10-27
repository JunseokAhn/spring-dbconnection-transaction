package com.example;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

class ConnectionUtilTest {

    @Test
    void getConnection(){
        Connection driverManagerCon = ConnectionUtil.getConnection(null);
        assertThat(driverManagerCon).isNotNull();
        Connection driverDataSourceCon = ConnectionUtil.getConnection(ConnectionUtil.getDriverDataSource());
        assertThat(driverDataSourceCon).isNotNull();
        Connection hikariDataSourceCon = ConnectionUtil.getConnection(ConnectionUtil.getHikariDataSource());
        assertThat(hikariDataSourceCon).isNotNull();
    }

    @Test
    void getDataSource(){
        HikariDataSource dataSource = ConnectionUtil.getHikariDataSource();
        assertThat(dataSource).isNotNull();
    }
    @Test
    void getDriverManagerDataSource(){
        DriverManagerDataSource dataSource = ConnectionUtil.getDriverDataSource();
        assertThat(dataSource).isNotNull();
    }

    @Test
    void getDataSourceConnection() throws SQLException, InterruptedException {
        DataSource dataSource = ConnectionUtil.getHikariDataSource();
        Connection connection = dataSource.getConnection();
        assertThat(connection).isNotNull();
    }
}
