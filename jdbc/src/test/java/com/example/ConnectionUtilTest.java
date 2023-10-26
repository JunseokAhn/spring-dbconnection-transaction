package com.example;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.*;

class ConnectionUtilTest {

    @Test
    void getConnection(){
        Connection connection = ConnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
