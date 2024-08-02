package com.codingrecipe.service;

import com.codingrecipe.dto.MemberDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    public void save(MemberDTO memberDTO) {
        // 회원 저장 로직 구현
    }

    public MemberDTO findByEmail(String email) {
        // 이메일로 회원 찾기 로직 구현
        return null;
    }

    public List<MemberDTO> findAll() {
        // 모든 회원 찾기 로직 구현
        return null;
    }

    public MemberDTO findById(Long id) {
        // ID로 회원 찾기 로직 구현
        return null;
    }

    public void update(MemberDTO memberDTO) {
        // 회원 정보 업데이트 로직 구현
    }

    public void deleteById(Long id) {
        // ID로 회원 삭제 로직 구현
    }

    public String emailCheck(String memberEmail) {
        // 이메일 중복 체크 로직 구현
        return "OK";
    }

    public MemberDTO updateForm(String email) {
        // 회원 정보 수정 폼 로직 구현
        return null;
    }
}
