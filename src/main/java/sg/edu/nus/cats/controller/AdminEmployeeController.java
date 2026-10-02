package sg.edu.nus.cats.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.nus.cats.dto.EmployeeCreateRequest;
import sg.edu.nus.cats.dto.EmployeeDashboardResponse;
import sg.edu.nus.cats.dto.EmployeeResponse;
import sg.edu.nus.cats.dto.EmployeeUpdateRequest;
import sg.edu.nus.cats.service.EmployeeService;

@RestController
@RequestMapping("/api/admin/employees")
public class AdminEmployeeController {

	private final EmployeeService employeeService;

	public AdminEmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	// ADMIN CREATE EMPLOYEE
	@PostMapping
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
		EmployeeResponse response = employeeService.createEmployee(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	// GET ALL EMPLOYEES
	@GetMapping
	public List<EmployeeResponse> getAllEmployees() {
		return employeeService.getAllEmployees();
	}
	
	// GET 1 EMPLOYEE BY EMPLOYEE ID
	@GetMapping("/{employeeId}")
	public EmployeeResponse getEmployee(@PathVariable Long employeeId) {
		return employeeService.getEmployee(employeeId);
	}
	
	// UPDATE 1 EMPLOYE BY EMPLOYEE ID
	@PutMapping("/{employeeId}")
	public EmployeeResponse updateEmployee(@PathVariable Long employeeId, @Valid @RequestBody EmployeeUpdateRequest request) {
		return employeeService.updateEmployee(employeeId, request);
	}
	
}
