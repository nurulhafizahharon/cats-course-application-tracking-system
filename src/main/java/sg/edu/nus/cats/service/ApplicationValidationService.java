package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.dto.CourseApplicationRequest;
import sg.edu.nus.cats.entity.CourseApplication;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.enums.CourseCategory;
import sg.edu.nus.cats.repository.CourseApplicationRepository;

@Service
public class ApplicationValidationService {

	private final CourseApplicationRepository applicationRepository;

	public ApplicationValidationService(CourseApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	private static final Set<ApplicationStatus> OVERLAP_STATUSES = EnumSet.of(ApplicationStatus.APPLIED,
			ApplicationStatus.UPDATED, ApplicationStatus.APPROVED);

	public void validateOverlap(Employee employee, LocalDate startDate, LocalDate endDate, Long excludeApplicationId) {
		List<CourseApplication> overlapping = applicationRepository.findOverlappingApplications(employee, startDate,
				endDate, OVERLAP_STATUSES, excludeApplicationId);

		if (!overlapping.isEmpty()) {
			throw new IllegalArgumentException("Course dates overlap with an existing application");
		}
	}

	public void validate(CourseApplicationRequest request) {
		validateFee(request);
		validateHalfDay(request);
	}

	private void validateFee(CourseApplicationRequest request) {
		BigDecimal fee = request.fee();
		if (request.category() == CourseCategory.INTERNAL_TRAINING) {
			if (fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
				throw new IllegalArgumentException("Internal Training cannot have a course fee");
			}
			return;
		}

		if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException(
					"Course fee must be greater than zero for External Course and Professional Certification");
		}
	}

	private void validateHalfDay(CourseApplicationRequest request) {
		boolean shouldBeHalfDay = request.category() == CourseCategory.INTERNAL_TRAINING;

		if (request.halfDay() != shouldBeHalfDay) {
			throw new IllegalArgumentException(
					request.category() == CourseCategory.INTERNAL_TRAINING ? "Internal Training must be half day"
							: "Half-day session are only allowed for Internal Training");
		}
	}

}
