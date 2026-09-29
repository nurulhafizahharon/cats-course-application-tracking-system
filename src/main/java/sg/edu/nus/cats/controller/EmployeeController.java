package sg.edu.nus.cats.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.nus.cats.dto.EmployeeCreateRequest;
import sg.edu.nus.cats.dto.EmployeeResponse;
import sg.edu.nus.cats.service.EmployeeService;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
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
	


}
