import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Employee } from '../models/employee';

@Service()
export class EmployeeService {
  private http = inject(HttpClient);

  getEmployee(employeeId: number) {
    return this.http.get<Employee>(`http://localhost:8080/api/admin/employees/${employeeId}`);
  }
}
