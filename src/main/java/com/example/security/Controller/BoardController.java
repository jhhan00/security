package com.example.security.Controller;

import com.example.security.SimpleBoardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BoardController {
    @Autowired
    SimpleBoardDao sbd;

    @RequestMapping("/board")
    public String board(Model model) {
        List<Map<String, Object>> list = sbd.getBoardList();
        model.addAttribute("list",list);

        return "simple_board";
    }

    @RequestMapping("/board/read/{articleId}")
    public String ReadAnArticle(Authentication auth, Model model, @PathVariable("articleId") int articleId) {
        Map<String, Object> article = sbd.getAnArticle(articleId);
        model.addAttribute("article",article);

        return "simple_board_view";
    }

    @RequestMapping("/board/write")
    public String WriteAnArticle() {
        return "simple_board_create";
    }

    @RequestMapping(value="/board/proc/write", method=RequestMethod.POST)
    public String RegisterAnArticle(Authentication auth, String title, String content) {
        int result = 0;
        System.out.println("987");
        if(title != null && !title.equals("") && content != null && !content.equals("")) {
            Map<String, Object> article = new HashMap<>();
            article.put("username", auth.getName());
            article.put("title", title);
            article.put("content", content);
            result = sbd.InsertAnArticle(article);
        } else {
            System.err.println("Do not write Blank Article");
        }

        return "redirect:/board?writeResult="+result;
    }
}
