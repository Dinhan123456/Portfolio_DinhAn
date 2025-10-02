package com.canhan.portfolio.service.interfaces;

import com.canhan.portfolio.entity.Project;

import java.util.List;

public interface ProjectService {
    List<Project> getAllProjects();

    Project findById(Integer id);

    Project save(Project project);

    void deleteById(Integer id);
}
