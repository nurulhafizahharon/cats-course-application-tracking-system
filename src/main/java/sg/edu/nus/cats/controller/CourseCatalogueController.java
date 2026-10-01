package sg.edu.nus.cats.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.cats.dto.CourseCatalogueItemResponse;
import sg.edu.nus.cats.service.CourseCatalogueService;

@RestController
@RequestMapping("/api/course-catalogue")
public class CourseCatalogueController {
	
	private final CourseCatalogueService catalogueService;
	
	public CourseCatalogueController(CourseCatalogueService catalogueService) {
		this.catalogueService = catalogueService;
	}
	
	// GET ALL ACTIVE COURSES IN CATALOGUE
	@GetMapping
	public List<CourseCatalogueItemResponse> getActiveCourses() {
		return catalogueService.getActiveCourses();
	}

}
