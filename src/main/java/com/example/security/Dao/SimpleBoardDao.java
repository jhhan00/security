package com.example.security.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SimpleBoardDao {
    @Autowired
    JdbcTemplate jt;

    public List<Map<String, Object>> getBoardList() {
        String sql = "select * from simple_board order by Write_DT desc";

        return jt.query(sql, (rs, rowNum) -> {
           Map<String, Object> anArticle = new HashMap<>();

           anArticle.put("seq",rs.getInt(1));
            anArticle.put("username",rs.getString(2));
            anArticle.put("Write_DT",rs.getString(3));
            anArticle.put("title",rs.getString(4));

           return anArticle;
        });
    }

    public Map<String, Object> getNameAndAuthority(String name) {
        String sql = "select * from users where username=?";

        return jt.queryForObject(sql, new Object[] {name}, (rs, rowNum) -> {
           Map<String, Object> anAuthority = new HashMap<>();

           anAuthority.put("username", rs.getString(1));
            anAuthority.put("role", rs.getString(5));

            return anAuthority;
        });
    }

    public Map<String, Object> getAnArticle(int articleId) {
        String sql = "select * from simple_board where seq=?";

        return jt.queryForObject(sql, new Object[] {articleId}, (rs, rowNum) -> {
           Map<String, Object> anArticle = new HashMap<>();

           anArticle.put("seq",rs.getInt(1));
            anArticle.put("username",rs.getString(2));
            anArticle.put("Write_DT",rs.getString(3));
            anArticle.put("title",rs.getString(4));
            anArticle.put("content",rs.getString(5));

           return anArticle;
        });
    }

    public int InsertAnArticle(Map<String, Object> article) {
        String sql = "insert into simple_board values(0, ?, sysdate(), ?, ?)";

        return jt.update(sql, article.get("username"),
                                article.get("title"),
                                article.get("content"));
    }
}
