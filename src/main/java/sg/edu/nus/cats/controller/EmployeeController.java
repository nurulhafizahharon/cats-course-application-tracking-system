package sg.edu.nus.cats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.cats.dto.EmployeeDashboardResponse;
import sg.edu.nus.cats.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	// GET EMPLOYEE DASHBOARD DETAILS
		@GetMapping("/dashboard")
		public EmployeeDashboardResponse getEmployeeDashboard(@RequestParam String username) {
			return employeeService.getEmployeeDashboard(username);
		}

}
