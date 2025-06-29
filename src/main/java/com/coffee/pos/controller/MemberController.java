package com.coffee.pos.controller;

import com.coffee.pos.dto.MemberResponseDTO;
import com.coffee.pos.model.Member;
import com.coffee.pos.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/member")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    @GetMapping
    public List<MemberResponseDTO> findMember(@RequestParam String name, @RequestParam(defaultValue = "false") boolean includeOrder){
        return memberService.findMemberByName(name, includeOrder);
    }

    @GetMapping("/{id}")
    public MemberResponseDTO findMemberById(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean includeOrder) {
        return memberService.findMemberById(id, includeOrder);
    }
}
