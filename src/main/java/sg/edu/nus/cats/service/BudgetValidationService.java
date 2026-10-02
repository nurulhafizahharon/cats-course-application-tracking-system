package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.entity.CourseApplication;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.repository.CourseApplicationRepository;

@Service
public class BudgetValidationService {
	private final CourseApplicationRepository applicationRepository;

	private static final Set<ApplicationStatus> BUDGET_STATUSES = EnumSet.of(ApplicationStatus.APPLIED,
			ApplicationStatus.UPDATED, ApplicationStatus.APPROVED, ApplicationStatus.COMPLETED);

	public BudgetValidationService(CourseApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	public void validate(Employee employee, LocalDate courseStartDate, BigDecimal newFee, Long excludeApplicationId) {
		if (newFee == null || newFee.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		int year = courseStartDate.getYear();
		LocalDate yearStart = LocalDate.of(year, 1, 1);
		LocalDate yearEnd = LocalDate.of(year, 12, 31);

		List<CourseApplication> applications = applicationRepository.findByEmployeeYearAndStatuses(employee, yearStart,
				yearEnd, BUDGET_STATUSES, excludeApplicationId);

		BigDecimal usedBudget = applications.stream().map(CourseApplication::getFee).filter(fee -> fee != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal annualBudget = employee.getAnnualTrainingBudget();

		BigDecimal requestedTotal = usedBudget.add(newFee);

		if (requestedTotal.compareTo(annualBudget) > 0) {
			BigDecimal remaining = annualBudget.subtract(usedBudget);
			throw new IllegalArgumentException(
					"Application exceeds remaining annual training budget. Remaining budget: $" + remaining);
		}
	}
	
	public BigDecimal getRemainingBudget(Employee employee, LocalDate date) {
	int year = date.getYear();
		
		LocalDate yearStart = LocalDate.of(year, 1, 1);
		LocalDate yearEnd = LocalDate.of(year, 12, 31);
		
		List<CourseApplication> applications = applicationRepository.findByEmployeeYearAndStatuses(employee, yearStart, yearEnd, BUDGET_STATUSES, null);
		
		BigDecimal usedBudget = applications.stream().map(CourseApplication::getFee).filter(fee -> fee != null).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal annualBudget = employee.getAnnualTrainingBudget();
		
		return annualBudget.subtract(usedBudget);
	}
}
