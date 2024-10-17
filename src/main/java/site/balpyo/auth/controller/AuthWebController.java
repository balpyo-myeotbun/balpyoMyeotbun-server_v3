package site.balpyo.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.balpyo.auth.entity.User;
import site.balpyo.auth.repository.UserRepository;

import java.util.Optional;

@Controller
public class AuthWebController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/verify")
    public String verifyUser(@RequestParam(value = "uid", required = false)String uid , Model model) {
        model.addAttribute("uid", uid);
        return "verify";
    }

}
