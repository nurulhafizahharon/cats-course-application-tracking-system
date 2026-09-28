package sg.edu.nus.cats.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.cats.dto.CourseApplicationResponse;
import sg.edu.nus.cats.entity.CourseApplication;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.enums.Role;
import sg.edu.nus.cats.mapper.CourseApplicationMapper;
import sg.edu.nus.cats.repository.CourseApplicationRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;

@Service
@Transactional
public class ManagerApplicationService {

	private final CourseApplicationRepository applicationRepository;
	private final EmployeeRepository employeeRepository;
	private final CourseApplicationMapper applicationMapper;

	private static final Set<ApplicationStatus> PENDING_STATUSES = EnumSet.of(ApplicationStatus.APPLIED,
			ApplicationStatus.UPDATED);

	public ManagerApplicationService(CourseApplicationRepository applicationRepository,
			EmployeeRepository employeeRepository, CourseApplicationMapper applicationMapper) {
		this.applicationRepository = applicationRepository;
		this.employeeRepository = employeeRepository;
		this.applicationMapper = applicationMapper;
	}

	@Transactional(readOnly = true)
	public Page<CourseApplicationResponse> getPendingApplications(String username, Pageable pagebale) {
		Employee manager = getManager(username);

		return applicationRepository.findByEmployeeManagerAndStatusIn(manager, PENDING_STATUSES, pagebale)
				.map(applicationMapper::toResponse);
	}
	
	public CourseApplicationResponse approve(Long applicaitonId, String username, String reason) {
		Employee manager = getManager(username);
		
		CourseApplication application = getPendingApplication(applicaitonId, manager);
		
		validateReason(reason);
		
		application.setStatus(ApplicationStatus.APPROVED);
		application.setDecidedBy(manager);
		application.setDecisionDate(LocalDateTime.now());
		application.setManagerReason(reason);
		application.setLastUpdatedAt(LocalDateTime.now());
		
		CourseApplication approved = applicationRepository.save(application);
		
		return applicationMapper.toResponse(approved);
	}
	
	public CourseApplicationResponse reject(Long applicaitonId, String username, String reason) {
		Employee manager = getManager(username);
		
		CourseApplication application = getPendingApplication(applicaitonId, manager);
		
		validateReason(reason);
		
		application.setStatus(ApplicationStatus.REJECTED);
		application.setDecidedBy(manager);
		application.setDecisionDate(LocalDateTime.now());
		application.setManagerReason(reason);
		application.setLastUpdatedAt(LocalDateTime.now());
		
		CourseApplication rejected = applicationRepository.save(application);
		
		return applicationMapper.toResponse(rejected);
	}

	private Employee getManager(String username) {
		Employee manager = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Manager not found"));

		if (!manager.getRoles().contains(Role.MANAGER)) {
			throw new IllegalArgumentException("Employee does not have Manager role");
		}
		return manager;
	}

	private CourseApplication getPendingApplication(Long applicationId, Employee manager) {
		CourseApplication application = applicationRepository
				.findByApplicationIdAndEmployeeManager(applicationId, manager)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));
		
		if(application.getStatus() != ApplicationStatus.APPLIED && application.getStatus() != ApplicationStatus.UPDATED) {
			throw new IllegalArgumentException("Only pending applicaitons can be decided");
		}
		
		return application;
	}
	
	private void validateReason(String reason) {
		if(reason == null || reason.isBlank()) {
			throw new IllegalArgumentException("Manager reason is required");
		}
	}

}
