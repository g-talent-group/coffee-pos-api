package com.coffee.pos.repository;

import com.coffee.pos.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public List<Member> findByNameContainingIgnoreCase(String name);

}
