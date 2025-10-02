package com.canhan.portfolio.controller;

import com.canhan.portfolio.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SkillService skillService;
    private final EducationService educationService;
    private final ExperienceService experienceService;
    private final ProjectService projectService;
    private final MessageService messageService;

    @Autowired
    public AdminController(SkillService skillService,
            EducationService educationService,
            ExperienceService experienceService,
            ProjectService projectService,
            MessageService messageService) {
        this.skillService = skillService;
        this.educationService = educationService;
        this.experienceService = experienceService;
        this.projectService = projectService;
        this.messageService = messageService;
    }

    @RequestMapping({ "", "/" })
    public String showAdminIndex(Model model) {
        // Get real data counts from services
        int skillCount = skillService.getAllSkills().size();
        int educationCount = educationService.getAllEducationDetails().size();
        int experienceCount = experienceService.getAllWorkExperiences().size();
        int projectCount = projectService.getAllProjects().size();
        int messageCount = messageService.getAllMessages().size();

        // Add counts to model
        model.addAttribute("skillCount", skillCount);
        model.addAttribute("educationCount", educationCount);
        model.addAttribute("experienceCount", experienceCount);
        model.addAttribute("projectCount", projectCount);
        model.addAttribute("messageCount", messageCount);

        return "admin/admin-home";
    }
}