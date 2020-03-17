package com.example.security.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReportDao {
    @Autowired
    JdbcTemplate jt;

    public List<Map<String, Object>> getReportList() {
        String sql = "select * from report_table order by writeDate desc";

        return jt.query(sql, (rs, rowNum) -> {
            Map<String, Object> aReport = new HashMap<>();

            aReport.put("No", rs.getInt(1));
            aReport.put("username", rs.getString(2));
            aReport.put("writeDate", rs.getString(3));
            aReport.put("report_type", rs.getString(4));
            aReport.put("simpleDate", rs.getString(5));

            return aReport;
        });
    }

    public Map<String, String> getUserInfo(String name) {
        String sql = "select * from users where username = ?";

        return jt.queryForObject(sql, new Object[] {name}, (rs, rowNum) -> {
           Map<String, String> anAuthority = new HashMap<>();

           anAuthority.put("username",rs.getString(1));
            anAuthority.put("realName",rs.getString(4));
            anAuthority.put("role",rs.getString(5));

           return anAuthority;
        });
    }

    public List<Map<String, Object>> getReportTask(int r_no) {
        String sql = "select * from task_table where report_no = ?";

        return jt.query(sql, new Object[] {r_no}, (rs, rowNum) ->{
           Map<String, Object> task = new HashMap<>();

           task.put("username", rs.getString(2));
            task.put("simpleDate", rs.getString(3));
            task.put("report_type", rs.getString(4));
            task.put("kind", rs.getString(5));
            task.put("description", rs.getString(6));

           return task;
        });
    }

    public Map<String, Object> getReportInfo(int r_no) {
        String sql = "select * from report_table where No = ?";

        return jt.queryForObject(sql, new Object[] {r_no}, (rs, rowNum) -> {
           Map<String, Object> r_info = new HashMap<>();

           r_info.put("username",rs.getString(2));
            r_info.put("writeDate",rs.getString(3));
            r_info.put("report_type",rs.getString(4));
            r_info.put("simpleDate",rs.getString(5));

           return r_info;
        });
    }
}
