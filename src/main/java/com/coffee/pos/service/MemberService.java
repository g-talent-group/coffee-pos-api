package com.coffee.pos.service;

import com.coffee.pos.dto.MemberResponseDTO;
import com.coffee.pos.model.Member;
import com.coffee.pos.repository.MemberRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberService {
  private final MemberRepository memberRepository;

  @Autowired
  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  public Member createMember(Member member) {
    Member.initTime(member);
    log.info(member.toString());
    return memberRepository.save(member);
  }

  public List<MemberResponseDTO> findMemberByName(String name, boolean includeOrder) {
    return memberRepository.findByNameContainingIgnoreCase(name).stream()
        .map(member -> MemberResponseDTO.toDTO(member, includeOrder))
        .toList();
  }

  public MemberResponseDTO findMemberById(Long id, boolean includeOrder) {
    Member member =
        memberRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + 1d));
    return MemberResponseDTO.toDTO(member, includeOrder);
  }
}
