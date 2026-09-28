package sg.edu.nus.cats.mapper;

import org.springframework.stereotype.Component;

import sg.edu.nus.cats.dto.EmployeeResponse;
import sg.edu.nus.cats.entity.Employee;

@Component
public class EmployeeMapper {
	public EmployeeResponse toResponse(Employee employee) {

        Long managerId = null;
        String managerName = null;

        if (employee.getManager() != null) {
            managerId = employee.getManager().getEmployeeId();
            managerName = employee.getManager().getName();
        }

        return new EmployeeResponse(
                employee.getEmployeeId(),
                employee.getUsername(),
                employee.getName(),
                employee.getDateOfJoining(),
                employee.getAnnualTrainingBudget(),
                employee.getTrainingDayEntitlement(),
                employee.getDesignation(),
                employee.isActive(),
                employee.getRoles(),
                managerId,
                managerName
        );
    }

}
