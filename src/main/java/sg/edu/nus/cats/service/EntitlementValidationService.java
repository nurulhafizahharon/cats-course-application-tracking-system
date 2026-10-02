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
public class EntitlementValidationService {

	private final CourseApplicationRepository applicationRepository;

	private static final Set<ApplicationStatus> ENTITLEMENT_STATUSES = EnumSet.of(ApplicationStatus.APPLIED,
			ApplicationStatus.UPDATED, ApplicationStatus.APPROVED, ApplicationStatus.COMPLETED);

	public EntitlementValidationService(CourseApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	public void validate(Employee employee, LocalDate courseStartDate, BigDecimal newDuration, Long excludeApplicationId) {
		int year = courseStartDate.getYear();
		LocalDate yearStart = LocalDate.of(year, 1, 1);
		LocalDate yearEnd = LocalDate.of(year, 12, 31);

		List<CourseApplication> applications = applicationRepository.findByEmployeeYearAndStatuses(employee, yearStart,
				yearEnd, ENTITLEMENT_STATUSES, excludeApplicationId);

		BigDecimal usedDays = applications.stream().map(CourseApplication::getDurationDays)
				.filter(duration -> duration != null).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal entitlement = employee.getTrainingDayEntitlement();

		BigDecimal requestedTotal = usedDays.add(newDuration);

		if (requestedTotal.compareTo(entitlement) > 0) {
			BigDecimal remaining = entitlement.subtract(usedDays);
			throw new IllegalArgumentException(
					"Application exceeds remaining training-day entitlement. Remaining entitlement: " + remaining
							+ " day(s)");
		}
	}
	
	public BigDecimal getRemainingEntitlement(Employee employee, LocalDate date) {
		int year = date.getYear();
		
		LocalDate yearStart = LocalDate.of(year, 1, 1);
		LocalDate yearEnd = LocalDate.of(year, 12, 31);
		
		List<CourseApplication> applications = applicationRepository.findByEmployeeYearAndStatuses(employee, yearStart, yearEnd, ENTITLEMENT_STATUSES, null);
		
		BigDecimal usedDays = applications.stream().map(CourseApplication::getDurationDays).filter(duration -> duration != null).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal entitlement = employee.getTrainingDayEntitlement();
		return entitlement.subtract(usedDays);
		
	}

}
