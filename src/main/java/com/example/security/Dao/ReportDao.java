//package com.example.security.Dao;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//@Repository
//public class ReportDao {
//    @Autowired
//    JdbcTemplate jt;
//
//    public Map<String, String> getUserInfo(String name) {
//        String sql = "select * from users where username = ?";
//
//        return jt.queryForObject(sql, new Object[] {name}, (rs, rowNum) -> {
//           Map<String, String> anAuthority = new HashMap<>();
//
//           anAuthority.put("username",rs.getString(1));
//            anAuthority.put("realName",rs.getString(4));
//            anAuthority.put("role",rs.getString(5));
//
//           return anAuthority;
//        });
//    }
//
//}
