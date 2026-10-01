import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CourseApplicationService } from '../../../services/course-application-service';
import { CourseApplication } from '../../../models/course-application';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CourseApplicationRequest } from '../../../models/course-application-request';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-update-course-application',
  styleUrl: './update-course-application.scss',
  templateUrl: './update-course-application.html',
})
export class UpdateCourseApplication implements OnInit {
  private route = inject(ActivatedRoute);
  private courseApplicationService = inject(CourseApplicationService);
  private router = inject(Router);
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
      const isInternal = category === 'INTERNAL_TRAINING';
      const feeControl = this.updateForm.get('fee');
      const startDateControl = this.updateForm.get('startDate');
      const endDateControl = this.updateForm.get('endDate');

      this.updateForm.patchValue({
        halfDay: isInternal,
        // fee: isInternal ? 0 : this.updateForm.get('fee')?.value,
      });

      if (isInternal) {
        feeControl?.setValue(0);
        feeControl?.disable();

        // INTERNAL TRAINING IS A HALF-DAY, SINGLE DAY APPLICATION
        endDateControl?.setValue(startDateControl?.value ?? null);
        endDateControl?.disable();
      } else {
        feeControl?.enable();
        endDateControl?.enable();

        if (feeControl?.value === 0) {
          feeControl.setValue(null);
        }
      }
    });

    this.updateForm.get('startDate')?.valueChanges.subscribe((startDate) => {
      const category = this.updateForm.get('category')?.value;

      if (category === 'INTERNAL_TRAINING') {
        this.updateForm.get('endDate')?.setValue(startDate);
      }
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

        const isInternal = application.category === 'INTERNAL_TRAINING';

        const feeControl = this.updateForm.get('fee');
        const endDateControl = this.updateForm.get('endDate');

        if (isInternal) {
          feeControl?.setValue(0);
          feeControl?.disable();

          endDateControl?.setValue(application.startDate);
          endDateControl?.disable();
        } else {
          feeControl?.enable();
          endDateControl?.enable();
        }

        console.log('Update form:', this.updateForm.value);
      },
      error: (error) => {
        console.error('Failed to load application for update:', error);
        this.errorMessage.set('Unable to load course application.');
      },
    });
  }

  onSubmit(): void {
    if (this.updateForm.invalid) {
      return;
    }

    const application = this.application();

    if (!application) {
      return;
    }

    const formValue = this.updateForm.getRawValue();

    const request: CourseApplicationRequest = {
      courseTitle: formValue.courseTitle ?? '',
      category: formValue.category ?? '',
      trainingProviderId: formValue.trainingProviderId ?? 0,
      courseCatalogueItemId: formValue.courseCatalogueItemId,
      startDate: formValue.startDate ?? '',
      endDate: formValue.endDate ?? '',
      fee: formValue.fee ?? 0,
      halfDay: formValue.category === 'INTERNAL_TRAINING',
      justification: formValue.justification ?? '',
      workDissemination: formValue.workDissemination ?? '',
    };

    console.log('Application ID: ', application.applicationId);
    console.log('Update request: ', request);

    this.courseApplicationService
      .updateApplication(application.applicationId, 'aliceTheEmployee', request)
      .subscribe({
        next: (updatedApplication) => {
          console.log('Application updated successfully: ', updatedApplication);
          this.router.navigate(['/course-history', updatedApplication.applicationId]);
        },
        error: (error) => {
          console.error('Failed to update application: ', error);
          this.errorMessage.set('Unable to update course application.');
        },
      });
  }

  cancelUpdate(): void {
    const application = this.application();

    if (!application) {
      return;
    }

    this.router.navigate(['/course-history', application.applicationId]);
  }
}
