package com.coffee.pos.controller.user;

import com.coffee.pos.dto.MemberResponseDTO;
import com.coffee.pos.model.Member;
import com.coffee.pos.service.MemberService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user/member")
public class UserMemberController {
  private final MemberService memberService;

  @Autowired
  public UserMemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping
  public Member createMember(@RequestBody Member member) {
    return memberService.createMember(member);
  }

  @GetMapping
  public List<MemberResponseDTO> findMember(
      @RequestParam(defaultValue = "") String name,
      @RequestParam(defaultValue = "false") boolean includeOrder) {
    return memberService.findMemberByName(name, includeOrder);
  }

  @GetMapping("/{id}")
  public MemberResponseDTO findMemberById(
      @PathVariable Long id, @RequestParam(defaultValue = "false") boolean includeOrder) {
    return memberService.findMemberById(id, includeOrder);
  }
}
