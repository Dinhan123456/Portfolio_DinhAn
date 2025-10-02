package com.canhan.portfolio.dao;

import com.canhan.portfolio.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    List<Skill> findByCategory(String category);
}