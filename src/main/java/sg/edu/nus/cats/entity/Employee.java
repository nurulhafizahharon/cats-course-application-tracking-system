package sg.edu.nus.cats.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.edu.nus.cats.enums.Role;

@Entity
@Table(name = "employee")
@Data
@NoArgsConstructor
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long employeeId;
	
	@Column(nullable = false, unique = true)
	private String username;
	
	private String password;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "employee_roles",
						joinColumns = @JoinColumn(name = "employee_id"))
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Set<Role> roles;
	
	@Column(nullable = false)
	private boolean active = true;
	
	private String name;
	
	private LocalDate dateOfJoining;
	
	private BigDecimal annualTrainingBudget;
	
	private BigDecimal trainingDayEntitlement;
	
	private String designation;
	
	@ManyToOne
	@JoinColumn(name = "manager_id")
	private Employee manager;
	
}
