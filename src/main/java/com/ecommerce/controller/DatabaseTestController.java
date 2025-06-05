package com.ecommerce.controller;

import com.ecommerce.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/mysql/connect")
    public Result<?> testConnection() {
        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            String status = "MySQL连接成功!" +
                    "\nURL: " + conn.getMetaData().getURL() +
                    "\nDatabase: " + conn.getCatalog();
            return Result.success(status);
        } catch (SQLException e) {
            return Result.fail(500, "MySQL连接失败: " + e.getMessage());
        }
    }

    @PostMapping("/mysql/crud")
    public Result<?> testCRUD() {
        try {
            // Create
            jdbcTemplate.update("INSERT INTO test_table (test_value) VALUES (?)", "testData");

            // Read
            String value = jdbcTemplate.queryForObject(
                    "SELECT test_value FROM test_table ORDER BY id DESC LIMIT 1",
                    String.class);

            // Update
            jdbcTemplate.update("UPDATE test_table SET test_value = ? WHERE test_value = ?",
                    "updatedData", "testData");

            // Delete
            jdbcTemplate.update("DELETE FROM test_table");

            return Result.success("MySQL CRUD操作测试成功!");
        } catch (Exception e) {
            return Result.fail(500, "MySQL操作失败: " + e.getMessage());
        }
    }
}