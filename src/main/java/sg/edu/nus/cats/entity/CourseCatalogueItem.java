package sg.edu.nus.cats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.edu.nus.cats.enums.CourseCategory;

@Data
@NoArgsConstructor
@Entity
@Table(name = "course_catalogue_item")
public class CourseCatalogueItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courseId;

	@Column(nullable = false)
	private String title;

	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CourseCategory category;

	@ManyToOne
	@JoinColumn(name = "provider_id", nullable = false)
	private TrainingProvider provider;

	@Column(nullable = false)
	private boolean active = true;

}
