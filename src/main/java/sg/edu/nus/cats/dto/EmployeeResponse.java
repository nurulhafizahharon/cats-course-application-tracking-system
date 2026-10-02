package sg.edu.nus.cats.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import sg.edu.nus.cats.enums.Role;

public record EmployeeResponse(Long employeeId, String username, String name, LocalDate dateOfJoining,
		BigDecimal annualTrainingBudget, BigDecimal trainingDayEntitlement, String designation, boolean active, Set<Role> roles, Long managerId,
		String managerName) {

}
