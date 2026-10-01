import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CourseApplicationService } from '../../../services/course-application-service';
import { CourseApplication } from '../../../models/course-application';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialog } from '../../../components/confirm-dialog/confirm-dialog';

@Component({
  imports: [CurrencyPipe, DatePipe, MatCardModule, MatButtonModule, RouterLink],
  selector: 'app-course-application-details',
  styleUrl: './course-application-details.scss',
  templateUrl: './course-application-details.html',
})
export class CourseApplicationDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private courseApplicationService = inject(CourseApplicationService);
  private dialog = inject(MatDialog);

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
  }

  confirmDelete(): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Delete Course Application',
        message: 'Are you sure you want to delete this course application?',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        const application = this.application();

        if (!application) {
          return;
        }

        this.courseApplicationService
          .deleteApplication(application.applicationId, 'aliceTheEmployee')
          .subscribe({
            next: (deletedApplication) => {
              console.log('Application deleted: ', deletedApplication);
              this.application.set(deletedApplication);
            },
            error: (error) => {
              console.error('Failed to delete application', error);
              this.errorMessage.set('Unable to delete course application.');
            },
          });
      }
    });
  }
}
