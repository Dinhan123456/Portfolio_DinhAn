package com.canhan.portfolio.service.interfaces;

import com.canhan.portfolio.entity.Experience;

import java.util.List;

public interface ExperienceService {
    List<Experience> getAllWorkExperiences();

    Experience findById(Integer id);

    Experience save(Experience workExperience);

    void deleteById(Integer id);
}