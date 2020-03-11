package com.example.security.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class SimpleUserDao {
    @Autowired
    JdbcTemplate jt;

    public String GetPassword(String user_id) {
        String sql = "select password from users where username = ?";

        return jt.queryForObject(sql, new Object[]{user_id}, String.class);
    }

    public int UpdatePassword(String user_id, String password) {
        String sql = "update users set password = ? where username = ?";

        return jt.update(sql, password, user_id);
    }

    public int InsertUserInfo(Map<String, String> user) {
        String sql = "insert into users values(?,?,0,?,'USER')";

        return jt.update(sql,
                user.get("user_id"),
                user.get("user_password"),
                user.get("user_realname")
        );
    }

    public int UpdateEnabled(String userid) {
        String sql = "update users set enabled = 1 where username = ?";

        return jt.update(sql, userid);
    }
}
