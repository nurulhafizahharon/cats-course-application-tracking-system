package sg.edu.nus.cats.dto;

import sg.edu.nus.cats.enums.CourseCategory;

public record CourseCatalogueItemResponse(Long courseId, String title, String description, CourseCategory category,
		Long providerId, String providerName) {

}
