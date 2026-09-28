package sg.edu.nus.cats.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import sg.edu.nus.cats.enums.Role;

public record EmployeeCreateRequest(
		@NotBlank(message = "Username is required")
		String username,
		
		@NotBlank(message = "Password is required")
		String password,
		
		@NotBlank(message = "Employee name is required")
		String name,
		
		@NotNull(message = "Date of joining is required")
		LocalDate dateOfJoining,
		
		@NotNull(message = "Annual training budget is required")
        @PositiveOrZero(message = "Annual training budget cannot be negative")
        BigDecimal annualTrainingBudget,

        @NotNull(message = "Training day entitlement is required")
        @PositiveOrZero(message = "Training day entitlement cannot be negative")
        BigDecimal trainingDayEntitlement,

        @NotBlank(message = "Designation is required")
        String designation,

        @NotEmpty(message = "At least one role is required")
        Set<Role> roles,

        Long managerId
		
		) {

}
