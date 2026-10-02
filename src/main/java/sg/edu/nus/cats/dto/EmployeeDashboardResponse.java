package sg.edu.nus.cats.dto;

import java.math.BigDecimal;

public record EmployeeDashboardResponse(Long employeeId, String username, String name,
		BigDecimal annualTrainingBudget, BigDecimal trainingDayEntitlement, BigDecimal remainingTrainingBudget,
		BigDecimal remainingTrainingDayEntitlement) {

}
