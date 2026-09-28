package sg.edu.nus.cats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.entity.CourseCatalogueItem;
import sg.edu.nus.cats.entity.TrainingProvider;

public interface CourseCatalogueItemRepository extends JpaRepository<CourseCatalogueItem, Long> {
	
	List<CourseCatalogueItem> findByActiveTrue();

	List<CourseCatalogueItem> findByProviderAndActiveTrue(TrainingProvider provider);

}
