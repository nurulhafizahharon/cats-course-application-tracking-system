package sg.edu.nus.cats.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.edu.nus.cats.entity.CourseApplication;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.enums.ApplicationStatus;

public interface CourseApplicationRepository extends JpaRepository<CourseApplication, Long> {

	Page<CourseApplication> findByEmployee(Employee employee, Pageable pageable);
	
	Page<CourseApplication> findByEmployeeAndStatusNot(Employee employee, ApplicationStatus status, Pageable pageable);

	@Query("""
			SELECT ca
			FROM CourseApplication ca
			WHERE ca.employee = :employee
			AND ca.startDate >= :yearStart
			AND ca.startDate <= :yearEnd
			AND ca.status IN :statuses
			AND (:excludeApplicationId IS NULL
			OR ca.applicationId <> :excludeApplicationId)
			""")
	List<CourseApplication> findByEmployeeYearAndStatuses(@Param("employee") Employee employee,
			@Param("yearStart") LocalDate yearStart, @Param("yearEnd") LocalDate yearEnd,
			@Param("statuses") Collection<ApplicationStatus> statuses,
			@Param("excludeApplicationId") Long excludeApplicationId);

	@Query("""
			SELECT ca
			FROM CourseApplication ca
			WHERE ca.employee = :employee
			AND ca.status IN :statuses
			AND ca.startDate <= :newEndDate
			AND ca.endDate >= :newStartDate
			AND (:excludeApplicationId IS NULL
			OR ca.applicationId <> :excludeApplicationId)
			""")
	List<CourseApplication> findOverlappingApplications(@Param("employee") Employee employee,
			@Param("newStartDate") LocalDate newStartDate, @Param("newEndDate") LocalDate newEndDate,
			@Param("statuses") Collection<ApplicationStatus> statuses,
			@Param("excludeApplicationId") Long excludeApplicationId);

	Optional<CourseApplication> findByApplicationIdAndEmployee(Long applicationId, Employee employee);
	
	
	/*
	 * SELECT ca.* FROM course_application ca JOIN employee e ON ca.employee_id =
	 * e.employee_id WHERE e.manager_id = ? AND ca.status IN (...);
	 */
	Page<CourseApplication> findByEmployeeManagerAndStatusIn(Employee manager, Collection<ApplicationStatus> statuses, Pageable pageable);
	
	Optional<CourseApplication> findByApplicationIdAndEmployeeManager(Long applicationId,  Employee manager);

}
