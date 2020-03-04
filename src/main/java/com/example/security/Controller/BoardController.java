package com.example.security.Controller;

import com.example.security.Dao.SimpleBoardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    SimpleBoardDao sbd;

    @GetMapping
    public String board(Authentication auth, Model model) throws Exception {
        List<Map<String, Object>> list = sbd.getBoardList();
        model.addAttribute("list",list);
        Map<String, Object> authority = sbd.getNameAndAuthority(auth.getName());
        model.addAttribute("authority", authority);
        System.out.println("board " + auth.getName());

        return "board/simple_board";
    }

    @GetMapping("/read/{articleId}")
    public String ReadAnArticle(Authentication auth, Model model, @PathVariable("articleId") int articleId) {
        System.out.println("View " + auth.getName());
        Map<String, Object> article = sbd.getAnArticle(articleId);
        model.addAttribute("article",article);
        Map<String, Object> authority = sbd.getNameAndAuthority(auth.getName());
        model.addAttribute("authority", authority);

        return "board/simple_board_view";
    }

    @GetMapping("/write")
    public String WriteAnArticle(Model model) {
        return "board/simple_board_create";
    }

    @PostMapping("/write")
    public String RegisterAnArticle(Authentication auth, String title, String content) {
        int result = 0;
        System.out.println("987 " + auth.getName());
        if(title != null && !title.equals("") && content != null && !content.equals("")) {
            Map<String, Object> article = new HashMap<>();
            article.put("username", auth.getName());
            article.put("title", title);
            article.put("content", content);
            result = sbd.InsertAnArticle(article);
        } else {
            System.err.println("Do not write Blank Article");
        }

        return "redirect:/board";
    }
}
