package com.jcg.examples;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;

public class HikariCPTest {

    private static DataSource datasource;

    public static DataSource getDataSource() {
        if (datasource == null) {

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://127.0.0.1/mydb");
            config.setUsername("root");
            config.setPassword("MyNewPass4!");

            config.setMaximumPoolSize(10);
            config.setAutoCommit(true);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "true");
            config.setConnectionTestQuery("SELECT 1");

            datasource = new HikariDataSource(config);
        }
        return datasource;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            DataSource dataSource = HikariCPTest.getDataSource();
            connection = dataSource.getConnection();
            pstmt = connection.prepareStatement("INSERT INTO user (username, crc_username) VALUES (?,CRC32(?))");
            for (int j = 1; j <= 20000; j++) {


                for (int i = 0; i < 14000; i++) {
                    String username = UUID.randomUUID().toString().replace("-", "");
                    pstmt.setString(1, username);
                    pstmt.setString(2, username);
                    pstmt.addBatch();
                }
                int[] ints = pstmt.executeBatch();
                System.out.println("第 " + j + "次，插入成功条数 => " + ints.length);
                long end = System.currentTimeMillis();
                System.out.println("耗时: " + ((int) (end - start) / 1000) + " s");
            }
//            resultSet = pstmt.executeQuery();
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString(1) + "," + resultSet.getString(2) + "," + resultSet.getString(3));
//            }

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

    }

}
