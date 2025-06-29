package com.coffee.pos.repository;

import com.coffee.pos.model.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
  public List<Member> findByNameContainingIgnoreCase(String name);
}
