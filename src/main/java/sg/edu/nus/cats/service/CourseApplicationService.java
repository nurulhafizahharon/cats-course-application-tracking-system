package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sg.edu.nus.cats.dto.CompleteCourseRequest;
import sg.edu.nus.cats.dto.CourseApplicationRequest;
import sg.edu.nus.cats.dto.CourseApplicationResponse;
import sg.edu.nus.cats.entity.CourseApplication;
import sg.edu.nus.cats.entity.CourseCatalogueItem;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.entity.TrainingProvider;
import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.enums.CourseCategory;
import sg.edu.nus.cats.mapper.CourseApplicationMapper;
import sg.edu.nus.cats.repository.CourseApplicationRepository;
import sg.edu.nus.cats.repository.CourseCatalogueItemRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.TrainingProviderRepository;

@Service
@Transactional
public class CourseApplicationService {

	private final CourseApplicationRepository applicationRepository;
	private final EmployeeRepository employeeRepository;
	private final TrainingProviderRepository providerRepository;
	private final CourseCatalogueItemRepository catalogueRepository;
	private final TrainingDayCalculator trainingDayCalculator;
	private final EntitlementValidationService entitlementValidationService;
	private final BudgetValidationService budgetValidationService;
	private final ApplicationValidationService applicationValidationService;
	private final CourseApplicationMapper applicationMapper;

	public CourseApplicationService(CourseApplicationRepository applicationRepository,
			EmployeeRepository employeeRepository, TrainingProviderRepository providerRepository,
			CourseCatalogueItemRepository catalogueRepository, TrainingDayCalculator trainingDayCalculator,
			EntitlementValidationService entitlementValidationService, BudgetValidationService budgetValidationService,
			ApplicationValidationService applicationValidationService, CourseApplicationMapper applicationMapper) {
		this.applicationRepository = applicationRepository;
		this.employeeRepository = employeeRepository;
		this.providerRepository = providerRepository;
		this.catalogueRepository = catalogueRepository;
		this.trainingDayCalculator = trainingDayCalculator;
		this.entitlementValidationService = entitlementValidationService;
		this.budgetValidationService = budgetValidationService;
		this.applicationValidationService = applicationValidationService;
		this.applicationMapper = applicationMapper;
	}

	// CREATE/SUBMIT NEW APPLICATION
	public CourseApplicationResponse submit(CourseApplicationRequest request, String username) {

		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		TrainingProvider provider = providerRepository.findById(request.trainingProviderId())
				.orElseThrow(() -> new IllegalArgumentException("Training Provider not found."));

		CourseCatalogueItem catalogueItem = null;

		if (request.courseCatalogueItemId() != null) {
			catalogueItem = catalogueRepository.findById(request.courseCatalogueItemId())
					.orElseThrow(() -> new IllegalArgumentException("Caourse catalogue item not found."));
		}

		applicationValidationService.validate(request);

		BigDecimal durationDays = trainingDayCalculator.calculate(request.startDate(), request.endDate(),
				request.category(), request.halfDay());

		applicationValidationService.validateOverlap(employee, request.startDate(), request.endDate(), null);

		// CHECKING ANNUAL LIMITS
		entitlementValidationService.validate(employee, request.startDate(), durationDays, null);

		budgetValidationService.validate(employee, request.startDate(), request.fee(), null);

		CourseApplication application = new CourseApplication();
		application.setEmployee(employee);
		application.setApplicationDate(LocalDate.now());
		application.setCourseTitle(request.courseTitle());
		application.setCategory(request.category());
		application.setTrainingProvider(provider);
		application.setCourseCatalogueItem(catalogueItem);
		application.setStartDate(request.startDate());
		application.setEndDate(request.endDate());
		application.setDurationDays(durationDays);
		application.setFee(request.fee());
		application.setHalfDay(request.halfDay());
		application.setJustification(request.justification());
		application.setWorkDissemination(request.workDissemination());
		application.setStatus(ApplicationStatus.APPLIED);
		application.setLastUpdatedAt(LocalDateTime.now());

		CourseApplication submission = applicationRepository.save(application);

		return applicationMapper.toResponse(submission);
	}

	// GET ALL APPLICATIONS BY USERNAME -> HIDE THE APPLICATIONS WITH DELETE STATUS
	@Transactional(readOnly = true)
	public Page<CourseApplicationResponse> getApplications(String username, Pageable pageable) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		return applicationRepository.findByEmployeeAndStatusNot(employee, ApplicationStatus.DELETED, pageable)
				.map(applicationMapper::toResponse);
	}

	// GET 1 APPLICATION BY APPLICATION ID AND USERNAME
	@Transactional(readOnly = true)
	public CourseApplicationResponse getApplication(Long applicationId, String username) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		CourseApplication application = applicationRepository.findByApplicationIdAndEmployee(applicationId, employee)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));

		return applicationMapper.toResponse(application);
	}

	// UPDATE EXISTING APPLICATION BY APPLICATION ID AND USERNAME
	public CourseApplicationResponse update(Long applicationId, CourseApplicationRequest request, String username) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		CourseApplication application = applicationRepository.findByApplicationIdAndEmployee(applicationId, employee)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));

		if (application.getStatus() != ApplicationStatus.APPLIED
				&& application.getStatus() != ApplicationStatus.UPDATED) {
			throw new IllegalArgumentException("Only pending application can be updated");
		}

		TrainingProvider provider = providerRepository.findById(request.trainingProviderId())
				.orElseThrow(() -> new IllegalArgumentException("Training provider not found"));

		CourseCatalogueItem catalogueItem = null;

		if (request.courseCatalogueItemId() != null) {
			catalogueItem = catalogueRepository.findById(request.courseCatalogueItemId())
					.orElseThrow(() -> new IllegalArgumentException("Course catalogue item not found"));
		}

		applicationValidationService.validate(request);
		
		// CATEGORY BUSINESS RULE. INTERNAL -> FEE = 0
		boolean halfDay = request.category() == CourseCategory.INTERNAL_TRAINING;
		
		BigDecimal fee = halfDay ? BigDecimal.ZERO : request.fee();

		BigDecimal durationDays = trainingDayCalculator.calculate(request.startDate(), request.endDate(),
				request.category(), halfDay);

		applicationValidationService.validateOverlap(employee, request.startDate(), request.endDate(), applicationId);

		entitlementValidationService.validate(employee, request.startDate(), durationDays, applicationId);

		budgetValidationService.validate(employee, request.startDate(), fee, applicationId);

		application.setCourseTitle(request.courseTitle());
		application.setCategory(request.category());
		application.setTrainingProvider(provider);
		application.setCourseCatalogueItem(catalogueItem);
		application.setStartDate(request.startDate());
		application.setEndDate(request.endDate());
		application.setDurationDays(durationDays);
		application.setFee(fee);
		application.setHalfDay(halfDay);
		application.setJustification(request.justification());
		application.setWorkDissemination(request.workDissemination());
		application.setStatus(ApplicationStatus.UPDATED);
		application.setLastUpdatedAt(LocalDateTime.now());

		CourseApplication updated = applicationRepository.save(application);

		return applicationMapper.toResponse(updated);
	}

	// DELETE -> CHANGE APPLICATION STATUS TO DELETE BY APPLICATION ID AND USERNAME.
	// DELETE IS WHEN EMPLOYEE WITHDRAW/REMOVES A PENDING APPLICATION
	public CourseApplicationResponse deleteApplication(Long applicationId, String username) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		CourseApplication application = applicationRepository.findByApplicationIdAndEmployee(applicationId, employee)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));

		if (application.getStatus() != ApplicationStatus.APPLIED
				&& application.getStatus() != ApplicationStatus.UPDATED) {
			throw new IllegalArgumentException("Only pending application can be deleted");
		}

		application.setStatus(ApplicationStatus.DELETED);
		application.setLastUpdatedAt(LocalDateTime.now());

		CourseApplication deleted = applicationRepository.save(application);

		return applicationMapper.toResponse(deleted);
	}

	// CANCEL
	// CANCEL IS WHEN MANAGER ALREADY APPROVED THE APPLICATION AND EMPLOYEE CANCEL
	public CourseApplicationResponse cancelApplication(Long applicationId, String username) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		CourseApplication application = applicationRepository.findByApplicationIdAndEmployee(applicationId, employee)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));
		
		if(application.getStatus() != ApplicationStatus.APPROVED) {
			throw new IllegalArgumentException("Only approved application can be cancelled");
		}
		
		application.setStatus(ApplicationStatus.CANCELLED);
		application.setLastUpdatedAt(LocalDateTime.now());
		
		CourseApplication cancelled = applicationRepository.save(application);
		
		return applicationMapper.toResponse(cancelled); 
	}
	
	// COMPLETE
	// COMPLETE WHEN EMPLOYEE HAVE COMPLETED THE COURSE
	public CourseApplicationResponse completeApplication(Long applicationId, String username, CompleteCourseRequest request) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		CourseApplication application = applicationRepository.findByApplicationIdAndEmployee(applicationId, employee)
				.orElseThrow(() -> new IllegalArgumentException("Course application not found"));
		
		if(application.getStatus() != ApplicationStatus.APPROVED) {
			throw new IllegalArgumentException("Only approved application can be completed");
		}
		
		if (LocalDate.now().isBefore(application.getEndDate())) {
		    throw new IllegalArgumentException(
		            "Course cannot be completed before the course end date");
		}
	
		application.setExperienceComment(request.experienceComment());
		application.setStatus(ApplicationStatus.COMPLETED);
		application.setLastUpdatedAt(LocalDateTime.now());
		
		CourseApplication completed = applicationRepository.save(application);
		
		return applicationMapper.toResponse(completed);
	}

}
