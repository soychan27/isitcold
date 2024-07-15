package com.study.isitcold.controller;

import com.study.isitcold.model.User;
import com.study.isitcold.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping({"","/"})
    public String index(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "indexloggedin";
        }
        return "index";
    }

    @GetMapping("/myinfoForm")
    public String myinfoForm(){
        return "myinfoform";
    }

    @PostMapping("/myinfo")
    public String myinfo(@AuthenticationPrincipal UserDetails userDetails, User user) {
        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        if (currentUser != null) {
            currentUser.setCold_temp(user.getCold_temp());
            currentUser.setHot_temp(user.getHot_temp());
            userRepository.save(currentUser);
        }
        return "redirect:/";
    }


    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @GetMapping("/user")
    public @ResponseBody String user(){
        return "user";
    }
}
