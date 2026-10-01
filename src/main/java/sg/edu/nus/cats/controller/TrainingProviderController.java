package sg.edu.nus.cats.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.nus.cats.dto.TrainingProviderResponse;
import sg.edu.nus.cats.service.TrainingProviderService;

@RestController
@RequestMapping("/api/training-providers")
public class TrainingProviderController {
	
	private final TrainingProviderService providerService;
	
	public TrainingProviderController(TrainingProviderService providerService) {
		this.providerService = providerService;
	}
	
	// GET ALL ACTIVE TRAINING PROVIDERS
	@GetMapping
	public List<TrainingProviderResponse> getActiveProviders() {
		return providerService.getActiveProviders();
	}

}
