import { Component, inject, OnInit, signal } from '@angular/core';
import { Employee } from '../../../models/employee';
import { MatCardModule } from '@angular/material/card';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { EmployeeService } from '../../../services/employee-service';

@Component({
  imports: [MatCardModule, CurrencyPipe, DatePipe],
  selector: 'app-profile',
  styleUrl: './profile.scss',
  templateUrl: './profile.html',
})
export class Profile implements OnInit {
  private employeeService = inject(EmployeeService);

  ngOnInit(): void {
    this.employeeService.getEmployee(3).subscribe({
      next: (employee) => {
        console.log('Employee received from backend:', employee);

        this.employee.set(employee);
      },
      error: (error) => {
        console.error('Failed to load employee: ', error);
        this.errorMeassge.set('Unable to load employee profile.');
      },
    });
  }

  employee = signal<Employee | null>(null);
  errorMeassge = signal<string | null>(null);
}
