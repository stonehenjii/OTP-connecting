package com.codingrecipe.controller;

import com.codingrecipe.dto.MemberDTO;
import com.codingrecipe.service.CognitoService;
import com.codingrecipe.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final CognitoService cognitoService;

    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        memberService.save(memberDTO);
        return "login";
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session, Model model) {
        try {
            Map<String, Object> authResult = cognitoService.initiateAuth(memberDTO.getMemberEmail(), memberDTO.getMemberPassword());
            if ("SMS_MFA".equals(authResult.get("ChallengeName"))) {
                session.setAttribute("sessionToken", authResult.get("Session"));
                session.setAttribute("username", memberDTO.getMemberEmail());
                return "otp"; // OTP 입력 페이지로 이동
            } else {
                model.addAttribute("loginError", "Unexpected challenge: " + authResult.get("ChallengeName"));
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("loginError", "Login failed: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/member/verify-otp")
    public String verifyOtp(@RequestParam("otpCode") String otpCode, HttpSession session, Model model) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        String username = (String) session.getAttribute("username");

        try {
            Map<String, Object> challengeResult = cognitoService.respondToAuthChallenge(username, sessionToken, otpCode);
            if (challengeResult.containsKey("AuthenticationResult")) {
                session.setAttribute("loginEmail", username);
                return "redirect:/member/main";
            } else {
                model.addAttribute("otpError", "OTP verification failed");
                return "otp";
            }
        } catch (Exception e) {
            model.addAttribute("otpError", "OTP verification failed: " + e.getMessage());
            return "otp";
        }
    }

    @GetMapping("/member/main")
    public String main() {
        return "main";
    }

    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    @GetMapping("/member/{id}")
    public String findById(@PathVariable("id") Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "detail";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String) session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId();
    }

    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable("id") Long id) {
        memberService.deleteById(id);
        return "redirect:/member/";
    }

    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    @PostMapping("/member/email-check")
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        return memberService.emailCheck(memberEmail);
    }
}
