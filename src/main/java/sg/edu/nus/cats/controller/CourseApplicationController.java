package sg.edu.nus.cats.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.nus.cats.dto.CompleteCourseRequest;
import sg.edu.nus.cats.dto.CourseApplicationRequest;
import sg.edu.nus.cats.dto.CourseApplicationResponse;
import sg.edu.nus.cats.service.CourseApplicationService;

@RestController
@RequestMapping("/api/applications")
public class CourseApplicationController {

	private final CourseApplicationService applicationService;

	public CourseApplicationController(CourseApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	// CREATE / SUBMIT NEW APPLICATION
	@PostMapping
	public ResponseEntity<CourseApplicationResponse> submit(@Valid @RequestBody CourseApplicationRequest request,
			@RequestParam String username) {
		CourseApplicationResponse response = applicationService.submit(request, username);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// GET ALL APPLICATIONS BY USERNAME
	@GetMapping
	public Page<CourseApplicationResponse> getApplications(@RequestParam String username,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);

		return applicationService.getApplications(username, pageable);
	}

	// GET 1 APPLICATION BY APPLICATION ID AND USERNAME
	@GetMapping("/{applicationId}")
	public CourseApplicationResponse getApplication(@PathVariable Long applicationId, @RequestParam String username) {
		return applicationService.getApplication(applicationId, username);
	}

	// UPDATE 1 APPLICATION BY APPLICATON ID AND USERNAME
	@PutMapping("/{applicationId}")
	public CourseApplicationResponse update(@PathVariable Long applicationId,
			@Valid @RequestBody CourseApplicationRequest request, @RequestParam String username) {
		return applicationService.update(applicationId, request, username);
	}

	// DELETE 1 APPLICATION BY APPLICATION ID
	@DeleteMapping("{applicationId}")
	public CourseApplicationResponse deleteApplication(@PathVariable Long applicationId,
			@RequestParam String username) {
		return applicationService.deleteApplication(applicationId, username);
	}

	// CANCEL 1 APPLICATION BY APPLICATION ID
	@PatchMapping("/{applicationId}/cancel")
	public CourseApplicationResponse cancelApplication(@PathVariable Long applicationId,
			@RequestParam String username) {
		return applicationService.cancelApplication(applicationId, username);
	}

	// COMPLETE 1 APPLICATION BY APPLICATION ID
	@PatchMapping("/{applicationId}/complete")
	public CourseApplicationResponse completeApplication(@PathVariable Long applicationId,
			@RequestParam String username, @Valid @RequestBody CompleteCourseRequest request) {
		return applicationService.completeApplication(applicationId, username, request);
	}

}
