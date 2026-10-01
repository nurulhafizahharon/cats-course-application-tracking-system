import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseApplicationService } from '../../../services/course-application-service';
import { CourseApplication } from '../../../models/course-application';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-update-course-application',
  styleUrl: './update-course-application.scss',
  templateUrl: './update-course-application.html',
})
export class UpdateCourseApplication implements OnInit {
  private route = inject(ActivatedRoute);
  private courseApplicationService = inject(CourseApplicationService);

  private formBuilder = inject(FormBuilder);

  application = signal<CourseApplication | null>(null);
  errorMessage = signal<string | null>(null);

  updateForm = this.formBuilder.group({
    courseTitle: ['', Validators.required],
    category: ['', Validators.required],
    trainingProviderId: [0, Validators.required],
    courseCatalogueItemId: [null as number | null],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required],
    fee: [0, [Validators.required, Validators.min(0)]],
    halfDay: [false],
    justification: ['', Validators.required],
    workDissemination: [''],
  });

  ngOnInit(): void {
    const applicationId = Number(this.route.snapshot.paramMap.get('applicationId'));

    this.updateForm.get('category')?.valueChanges.subscribe((category) => {
      this.updateForm.patchValue({
        halfDay: category === 'INTERNAL_TRAINING',
      });
    });

    console.log('Update Application ID from URL:', applicationId);

    this.courseApplicationService.getApplication(applicationId, 'aliceTheEmployee').subscribe({
      next: (application) => {
        console.log('Application received in updated component:', application);
        this.application.set(application);

        this.updateForm.patchValue({
          courseTitle: application.courseTitle,
          category: application.category,
          trainingProviderId: application.trainingProviderId,
          courseCatalogueItemId: application.courseCatalogueItemId,
          startDate: application.startDate,
          endDate: application.endDate,
          fee: application.fee,
          halfDay: application.category === 'INTERNAL_TRAINING',
          justification: application.justification,
          workDissemination: application.workDissemination,
        });
        console.log('Update form:', this.updateForm.value);
      },
      error: (error) => {
        console.error('Failed to load application for update:', error);
        this.errorMessage.set('Unable to load course application.');
      },
    });
  }
}
