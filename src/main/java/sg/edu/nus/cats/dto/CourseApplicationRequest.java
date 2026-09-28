package sg.edu.nus.cats.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import sg.edu.nus.cats.enums.CourseCategory;

public record CourseApplicationRequest(
		@NotBlank(message = "Course title is required")
		String courseTitle,
		
		@NotNull(message = "Course category is required")
		CourseCategory category,
		
		@NotNull(message = "Training provider is required")
		Long trainingProviderId,
		
		Long courseCatalogueItemId,
		
		@NotNull(message = "Start date is required")
		LocalDate startDate,
		
		@NotNull(message = "End date is required")
		LocalDate endDate,
		
		@PositiveOrZero(message = "Course fee cannot be negative")
		BigDecimal fee,
		
		boolean halfDay,
		
		@NotBlank(message = "Justification is required")
		String justification,
		
		String workDissemination
) {

}
