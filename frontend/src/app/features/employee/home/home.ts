import { Component, inject, OnInit, signal } from '@angular/core';
import { EmployeeService } from '../../../services/employee-service';
import { Employee } from '../../../models/employee';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { EmployeeDashboard } from '../../../models/employee-dashboard';

@Component({
  imports: [MatCardModule, MatButtonModule, RouterLink, CurrencyPipe],
  selector: 'app-home',
  styleUrl: './home.scss',
  templateUrl: './home.html',
})
export class Home implements OnInit {
  private employeeService = inject(EmployeeService);

  // employee = signal<Employee | null>(null);
  dashboard = signal<EmployeeDashboard | null>(null);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.employeeService.getEmployeeDashboard('aliceTheEmployee').subscribe({
      next: (dashboard) => {
        console.log('Employee dashboard:', dashboard);
        this.dashboard.set(dashboard);
      },
      error: (error) => {
        console.error('Failed to load employee dashboard:', error);
        this.errorMessage.set('Unable to load employee dashboard.');
      },
    });
  }
}
