package sg.edu.nus.cats.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.nus.cats.dto.CourseApplicationResponse;
import sg.edu.nus.cats.dto.ManagerDecisionRequest;
import sg.edu.nus.cats.service.ManagerApplicationService;

@RestController
@RequestMapping("/api/manager/applications")
public class ManagerApplicationController {

	private final ManagerApplicationService managerApplicationService;

	public ManagerApplicationController(ManagerApplicationService managerApplicationService) {
		this.managerApplicationService = managerApplicationService;
	}

	@GetMapping
	public Page<CourseApplicationResponse> getPendingApplications(@RequestParam String username,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return managerApplicationService.getPendingApplications(username, pageable);
	}
	
	@PatchMapping("/{applicationId}/approve")
	public CourseApplicationResponse approve(@PathVariable Long applicationId, @RequestParam String username, @Valid @RequestBody ManagerDecisionRequest request) {
		return managerApplicationService.approve(applicationId, username, request.reason());
	}
	
	@PatchMapping("/{applicationId}/reject")
	public CourseApplicationResponse reject(@PathVariable Long applicationId, @RequestParam String username, @Valid @RequestBody ManagerDecisionRequest request) {
		return managerApplicationService.reject(applicationId, username, request.reason());
	}
}
