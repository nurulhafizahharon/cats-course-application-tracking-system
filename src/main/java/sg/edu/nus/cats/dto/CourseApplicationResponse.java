package sg.edu.nus.cats.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.enums.CourseCategory;

public record CourseApplicationResponse(
		Long applicationId,
		Long employeeId,
		String employeeName,
		LocalDate applicationDate,
		String courseTitle,
		CourseCategory category,
		Long trainingProviderId,
		String trainingProviderName,
		Long courseCatalogueItemId,
		LocalDate startDate,
		LocalDate endDate,
		BigDecimal durationDays,
		BigDecimal fee,
		boolean halfDay,
		String justification,
		String workDissemination,
		ApplicationStatus status,
		LocalDateTime lastUpdatedAt,
		String managerReason,
		Long decidedById,
		String decidedByName,
		LocalDateTime decisionDate,
		String experienceComment) {

}
