package com.example.qnote.cotroller;

import com.example.qnote.config.service.NoteService;
import com.example.qnote.config.service.UserService;
import com.example.qnote.dto.NoteDto;
import com.example.qnote.dto.UserDto;
import com.example.qnote.model.Note;
import java.util.Iterator;

import java.util.List;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.qnote.config.auth.PrincipalDetails;
import com.example.qnote.model.User;

@Controller
//@RequestMapping("Q-Note")
public class IndexController {

    private final NoteService noteService;

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IndexController(NoteService noteService, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.noteService = noteService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping({ "", "/" })
    public String index(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        User user = principal.getUser();
        UserDto userDto = UserDto.fromEntity(user);
        if (userDto != null) {
            model.addAttribute("user", userDto);
            List<NoteDto> noteDtos = noteService.notes(userDto.getId());
            model.addAttribute("noteList", noteDtos);
        }
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principal) {
        System.out.println("Principal : " + principal.getUser());
        System.out.println("OAuth2 : "+principal.getUser());
        // iterator 순차 출력 해보기
        Iterator<? extends GrantedAuthority> iter = principal.getAuthorities().iterator();
        while (iter.hasNext()) {
            GrantedAuthority auth = iter.next();
            System.out.println(auth.getAuthority());
        }

        return "유저 페이지입니다.";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "어드민 페이지입니다.";
    }

    //@PostAuthorize("hasRole('ROLE_MANAGER')")
    //@PreAuthorize("hasRole('ROLE_MANAGER')")
    @Secured("ROLE_MANAGER")
    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "매니저 페이지입니다.";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/join")
    public String join() {
        return "join";
    }

    @PostMapping("/joinProc")
    public String joinProc(User user) {
        System.out.println("회원가입 진행 : " + user);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        user.setRole("ROLE_USER");
        userService.save(user);
        return "redirect:/login";
    }
}