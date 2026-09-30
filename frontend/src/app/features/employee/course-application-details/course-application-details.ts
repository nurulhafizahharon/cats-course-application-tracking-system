import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseApplicationService } from '../../../services/course-application-service';
import { CourseApplication } from '../../../models/course-application';

@Component({
  imports: [],
  selector: 'app-course-application-details',
  styleUrl: './course-application-details.scss',
  templateUrl: './course-application-details.html',
})
export class CourseApplicationDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private courseApplicationService = inject(CourseApplicationService);
  application = signal<CourseApplication | null>(null);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const applicationId = Number(this.route.snapshot.paramMap.get('applicationId'));

    this.courseApplicationService.getApplication(applicationId, 'aliceTheEmployee').subscribe({
      next: (application) => {
        console.log('Application received from backend: ', application);
        this.application.set(application);
      },
      error: (error) => {
        console.error('Failed to load application: ', error);
        this.errorMessage.set('Unable to load course application.');
      },
    });

    console.log('Application ID from URL: ', applicationId);
  }
}
