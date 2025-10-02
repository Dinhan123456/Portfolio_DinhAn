package com.canhan.portfolio.service;

import com.canhan.portfolio.dao.ProjectRepository;
import com.canhan.portfolio.entity.Project;
import com.canhan.portfolio.service.interfaces.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderByStartDateDesc();
    }

    @Override
    public Project findById(Integer id) {
        Optional<Project> tempProject = projectRepository.findById(id);
        if(tempProject.isPresent())
            return tempProject.get();
        else
            throw new RuntimeException("Experience details not found in database");
    }

    @Override
    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        projectRepository.deleteById(id);
    }
}
