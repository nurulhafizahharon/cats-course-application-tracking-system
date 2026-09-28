package sg.edu.nus.cats.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "training_provider")
public class TrainingProvider {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long providerId;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(nullable = false)
	private boolean active;

}
