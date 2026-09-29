package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sg.edu.nus.cats.dto.EmployeeCreateRequest;
import sg.edu.nus.cats.dto.EmployeeResponse;
import sg.edu.nus.cats.dto.EmployeeUpdateRequest;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.mapper.EmployeeMapper;
import sg.edu.nus.cats.repository.EmployeeRepository;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final EmployeeMapper employeeMapper;
	private final PasswordEncoder passwordEncoder;

	public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
			PasswordEncoder passwordEncoder) {
		this.employeeRepository = employeeRepository;
		this.employeeMapper = employeeMapper;
		this.passwordEncoder = passwordEncoder;
	}

	// CREATE EMPLOYEE
	public EmployeeResponse createEmployee(EmployeeCreateRequest request) {

		if (employeeRepository.existsByUsername(request.username())) {
			throw new IllegalArgumentException("Username already exists");
		}

		Employee manager = null;

		if (request.managerId() != null) {
			manager = employeeRepository.findById(request.managerId())
					.orElseThrow(() -> new IllegalArgumentException("Manager not found"));
		}

		Employee employee = new Employee();

		employee.setUsername(request.username());
		employee.setPassword(passwordEncoder.encode(request.password()));
		employee.setName(request.name());
		employee.setDateOfJoining(request.dateOfJoining());
		employee.setAnnualTrainingBudget(request.annualTrainingBudget());
		employee.setTrainingDayEntitlement(request.trainingDayEntitlement());
		employee.setDesignation(request.designation());
		employee.setRoles(request.roles());
		employee.setManager(manager);
		employee.setActive(true);

		Employee savedEmployee = employeeRepository.save(employee);

		return employeeMapper.toResponse(savedEmployee);
	}

	// GET ALL EMPLOYEES
	public List<EmployeeResponse> getAllEmployees() {
		return employeeRepository.findAll().stream().map(employeeMapper::toResponse).toList();
	}

	// GET 1 EMPLOYEE BY EMPLOYEE ID
	public EmployeeResponse getEmployee(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));
		return employeeMapper.toResponse(employee);
	}

	// UPDATE 1 EMPLOYEE BY EMPLOYEE ID
	public EmployeeResponse updateEmployee(Long employeeId, EmployeeUpdateRequest request) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		Employee manager = null;

		if (request.managerId() != null && request.managerId().equals(employeeId)) {
			throw new IllegalArgumentException("Employee cannot be their own manager");
		}

		if (request.managerId() != null) {
			manager = employeeRepository.findById(request.managerId())
					.orElseThrow(() -> new IllegalArgumentException("Manager not found"));
		}

		employee.setName(request.name());
		employee.setDateOfJoining(request.dateOfJoining());
		employee.setAnnualTrainingBudget(request.annualTrainingBudget());
		employee.setTrainingDayEntitlement(request.trainingDayEntitlement());
		employee.setDesignation(request.designation());
		employee.setRoles(request.roles());
		employee.setManager(manager);
		employee.setActive(request.active());

		Employee updatedEmployee = employeeRepository.save(employee);

		return employeeMapper.toResponse(updatedEmployee);
	}

}
