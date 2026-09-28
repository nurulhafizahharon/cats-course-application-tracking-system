package sg.edu.nus.cats.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.nus.cats.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Optional<Employee> findByUsername(String username);
	
	List<Employee> findByManager(Employee manager);
	
	boolean existsByUsername(String username);
}
