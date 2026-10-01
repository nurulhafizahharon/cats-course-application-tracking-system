package sg.edu.nus.cats.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.dto.TrainingProviderResponse;
import sg.edu.nus.cats.repository.TrainingProviderRepository;

@Service
public class TrainingProviderService {

	private final TrainingProviderRepository providerRepository;

	public TrainingProviderService(TrainingProviderRepository providerRepository) {
		this.providerRepository = providerRepository;
	}

	
	// GET ALL ACTIVE TRAINING PROVIDERS
	public List<TrainingProviderResponse> getActiveProviders() {

		return providerRepository.findByActiveTrue().stream()
				.map(provider -> new TrainingProviderResponse(provider.getProviderId(), provider.getName())).toList();
	}
}
