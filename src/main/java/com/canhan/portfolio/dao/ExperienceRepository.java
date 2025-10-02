package com.canhan.portfolio.dao;

import com.canhan.portfolio.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Integer> {

    public List<Experience> findAllByOrderByEndDateDesc();
}