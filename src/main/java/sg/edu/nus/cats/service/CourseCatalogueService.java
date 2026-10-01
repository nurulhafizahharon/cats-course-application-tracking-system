package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.dto.CourseCatalogueItemResponse;
import sg.edu.nus.cats.repository.CourseCatalogueItemRepository;

@Service
public class CourseCatalogueService {

	private final CourseCatalogueItemRepository catalogueRepository;

	public CourseCatalogueService(CourseCatalogueItemRepository catalogRepository) {
		this.catalogueRepository = catalogRepository;
	}

	// GET ALL ACTIVE COURSES IN CATALOGUE
	public List<CourseCatalogueItemResponse> getActiveCourses() {
		return catalogueRepository.findByActiveTrue().stream()
				.map(course -> new CourseCatalogueItemResponse(course.getCourseId(), course.getTitle(),
						course.getDescription(), course.getCategory(), course.getProvider().getProviderId(),
						course.getProvider().getName()))
				.toList();
	}

}
