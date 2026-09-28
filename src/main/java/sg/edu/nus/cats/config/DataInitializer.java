package sg.edu.nus.cats.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sg.edu.nus.cats.entity.CourseCatalogueItem;
import sg.edu.nus.cats.entity.Employee;
import sg.edu.nus.cats.entity.TrainingProvider;
import sg.edu.nus.cats.enums.CourseCategory;
import sg.edu.nus.cats.enums.Role;
import sg.edu.nus.cats.repository.CourseCatalogueItemRepository;
import sg.edu.nus.cats.repository.EmployeeRepository;
import sg.edu.nus.cats.repository.TrainingProviderRepository;

@Configuration
public class DataInitializer {

	@Bean
	CommandLineRunner initEmployees(EmployeeRepository employeeRepository,
			TrainingProviderRepository trainingProviderRepository,
			CourseCatalogueItemRepository courseCatalogueItemRepository) {
		return args -> {
//			if(employeeRepository.count() > 0) {
//				return;
//			}
			if (employeeRepository.count() == 0) {
				// CREATE ADMIN ACCOUNT
				Employee admin = new Employee();
				admin.setUsername("admin");
				admin.setPassword("password");
				admin.setName("admin");
				admin.setDateOfJoining(LocalDate.of(2020, 1, 1));
				admin.setDesignation("Administrator");
				admin.setActive(true);
				admin.setRoles(Set.of(Role.ADMIN, Role.EMPLOYEE));

				// SAVE TO DATABASE
				employeeRepository.save(admin);

				// CREATE MANAGER ACCOUNT
				Employee manager = new Employee();
				manager.setUsername("bobTheManager");
				manager.setPassword("password");
				manager.setName("Bob");
				manager.setDateOfJoining(LocalDate.of(2021, 1, 1));
				manager.setDesignation("Manager");
				manager.setAnnualTrainingBudget(new BigDecimal("3000.00"));
				manager.setTrainingDayEntitlement(new BigDecimal("10.0"));
				manager.setActive(true);
				manager.setRoles(Set.of(Role.MANAGER, Role.EMPLOYEE));

				// SAVE TO DATABASE
				employeeRepository.save(manager);

				// CREATE EMPLOYEE ACCOUNT
				Employee employee = new Employee();
				employee.setUsername("aliceTheEmployee");
				employee.setPassword("password");
				employee.setName("Alice");
				employee.setDateOfJoining(LocalDate.of(2024, 1, 1));
				employee.setDesignation("Professional");
				employee.setAnnualTrainingBudget(new BigDecimal("2000.00"));
				employee.setTrainingDayEntitlement(new BigDecimal("10.0"));
				employee.setActive(true);
				employee.setRoles(Set.of(Role.EMPLOYEE));

				// SET MANAGER FOR ALICE
				employee.setManager(manager);

				// SAVE TO DATABASE
				employeeRepository.save(employee);
			}

			if (trainingProviderRepository.count() == 0) {
				TrainingProvider nusIss = new TrainingProvider();

				nusIss.setName("NUS-ISS");
				nusIss.setActive(true);

				trainingProviderRepository.save(nusIss);

				TrainingProvider ntuc = new TrainingProvider();

				ntuc.setName("NTUC LearningHub");
				ntuc.setActive(true);

				trainingProviderRepository.save(ntuc);
			}

			if (courseCatalogueItemRepository.count() == 0) {
				TrainingProvider nusIss = trainingProviderRepository.findAll().stream()
						.filter(p -> p.getName().equals("NUS-ISS")).findFirst().orElseThrow();

				CourseCatalogueItem angular = new CourseCatalogueItem();

				angular.setTitle("Angular Development");
				angular.setDescription("Introduction to Angular web development");
				angular.setCategory(CourseCategory.EXTERNAL_COURSE);
				angular.setProvider(nusIss);
				angular.setActive(true);

				courseCatalogueItemRepository.save(angular);

				CourseCatalogueItem springBoot = new CourseCatalogueItem();

				springBoot.setTitle("Spring Boot Development");
				springBoot.setDescription("Developing web applications with Spring Boot");
				springBoot.setCategory(CourseCategory.EXTERNAL_COURSE);
				springBoot.setProvider(nusIss);
				springBoot.setActive(true);

				courseCatalogueItemRepository.save(springBoot);
			}

		};
	}
}
