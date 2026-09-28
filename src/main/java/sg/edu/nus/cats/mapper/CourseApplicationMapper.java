package sg.edu.nus.cats.mapper;

import org.springframework.stereotype.Component;

import sg.edu.nus.cats.dto.CourseApplicationResponse;
import sg.edu.nus.cats.entity.CourseApplication;

@Component
public class CourseApplicationMapper {
	
	public CourseApplicationResponse toResponse(CourseApplication application) {
		Long catalogueItemId = null;

		if (application.getCourseCatalogueItem() != null) {
			catalogueItemId = application.getCourseCatalogueItem().getCourseId();
		}

		Long decidedById = null;
		String decidedByName = null;

		if (application.getDecidedBy() != null) {
			decidedById = application.getDecidedBy().getEmployeeId();
			decidedByName = application.getDecidedBy().getName();
		}

		return new CourseApplicationResponse(application.getApplicationId(), application.getEmployee().getEmployeeId(),
				application.getEmployee().getName(), application.getApplicationDate(), application.getCourseTitle(),
				application.getCategory(), application.getTrainingProvider().getProviderId(),
				application.getTrainingProvider().getName(), catalogueItemId, application.getStartDate(),
				application.getEndDate(), application.getDurationDays(), application.getFee(), application.isHalfDay(),
				application.getJustification(), application.getWorkDissemination(), application.getStatus(),
				application.getLastUpdatedAt(), application.getManagerReason(), decidedById, decidedByName,
				application.getDecisionDate(), application.getExperienceComment());
	}

}
