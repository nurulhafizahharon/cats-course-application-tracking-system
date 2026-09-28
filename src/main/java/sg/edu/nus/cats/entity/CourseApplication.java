package sg.edu.nus.cats.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import sg.edu.nus.cats.enums.ApplicationStatus;
import sg.edu.nus.cats.enums.CourseCategory;

@Entity
@Data
@NoArgsConstructor
@Table(name="course_application")
public class CourseApplication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;
	
	// EMPLOYEE SUBMIT
	@ManyToOne
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	
	@Column(nullable = false)
	private LocalDate applicationDate;
	
	// COURSE DETAILS
	@Column(nullable = false)
	private String courseTitle;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CourseCategory category;
	
	@ManyToOne
	@JoinColumn(name = "provider_id", nullable = false)
	private TrainingProvider trainingProvider;
	
	// IF EMPLOYEE APPLY FROM THE COURSE CATALOGUE
	@ManyToOne
	@JoinColumn(name = "catalogue_item_id")
	private CourseCatalogueItem courseCatalogueItem;
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	@Column(nullable = false)
	private LocalDate endDate;
	
	private BigDecimal durationDays;
	
	private BigDecimal fee;
	
	private boolean halfDay = false;
	
	// EMPLOYEE REASONS
	@Column(length = 2000)
	private String justification;
	
	@Column(length = 2000)
	private String workDissemination;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ApplicationStatus status;
	
	private LocalDateTime lastUpdatedAt;
	
	// MANAGER DECISION
	@Column(length = 2000)
	private String managerReason;
	
	@ManyToOne
	@JoinColumn(name = "decided_by")
	private Employee decidedBy;
	
	private LocalDateTime decisionDate;
	
	// EXPRIENCE AFTER COURSE COMPLETION
	@Column(length = 2000)
	private String experienceComment;
	

}
