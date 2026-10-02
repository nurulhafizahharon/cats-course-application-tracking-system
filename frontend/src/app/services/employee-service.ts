import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Employee } from '../models/employee';
import { EmployeeDashboard } from '../models/employee-dashboard';

@Service()
export class EmployeeService {
  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api';

  getEmployee(employeeId: number) {
    return this.http.get<Employee>(`${this.apiUrl}/admin/employees/${employeeId}`);
  }

  getEmployeeDashboard(username: string) {
    const params = new HttpParams().set('username', username);

    return this.http.get<EmployeeDashboard>(`${this.apiUrl}/employees/dashboard`, { params });
  }
}
