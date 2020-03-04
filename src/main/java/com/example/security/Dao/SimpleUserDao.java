package com.example.security.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class SimpleUserDao {
    @Autowired
    JdbcTemplate jt;

    public int InsertUserInfo(Map<String, String> user) {
        String sql = "insert into users values(?,?,1,?)";

        return jt.update(sql,
                user.get("user_id"),
                user.get("user_password"),
                user.get("user_realname")
        );
    }

    public int InsertAuthorityInfo(String user_id) {
        String sql = "insert into authorities values(?,'USER')";

        return jt.update(sql, user_id);
    }
}
