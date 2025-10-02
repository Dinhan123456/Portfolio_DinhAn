package com.canhan.portfolio.service.interfaces;

import com.canhan.portfolio.entity.Education;

import java.util.List;

public interface EducationService {
    List<Education> getAllEducationDetails();

    Education findById(int theId);

    Education save(Education theEducation);

    void deleteById(int theId);
}