import { Component, inject, OnInit, signal } from '@angular/core';
import { CourseApplicationService } from '../../../services/course-application-service';
import { CourseApplication } from '../../../models/course-application';
import { DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  imports: [MatTableModule, MatButtonModule, RouterLink, DatePipe],
  selector: 'app-course-history',
  styleUrl: './course-history.scss',
  templateUrl: './course-history.html',
})
export class CourseHistory implements OnInit {
  private courseApplicationService = inject(CourseApplicationService);

  ngOnInit(): void {
    this.courseApplicationService.getApplications('aliceTheEmployee').subscribe({
      next: (page) => {
        console.log('course applications received: ', page);

        this.applications.set(page.content);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load course applications history.');
        this.loading.set(false);
      },
    });
  }

  applications = signal<CourseApplication[]>([]);
  errorMessage = signal<string | null>(null);
  loading = signal(true);

  displayedColumns: string[] = [
    'courseTitle',
    'category',
    'applicationDate',
    'startDate',
    'status',
    'details',
  ];
}
