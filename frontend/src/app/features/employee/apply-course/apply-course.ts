import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CourseApplicationService } from '../../../services/course-application-service';
import { Router } from '@angular/router';
import { CourseCatalogueItem } from '../../../models/course-catalogue-item';
import { TrainingProvider } from '../../../models/training-provider';
import { CourseApplicationRequest } from '../../../models/course-application-request';
import { ApiErrorResponse } from '../../../models/api-error-response';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-apply-course',
  styleUrl: './apply-course.scss',
  templateUrl: './apply-course.html',
})
export class ApplyCourse implements OnInit {
  private formBuilder = inject(FormBuilder);
  private courseApplicationService = inject(CourseApplicationService);
  private router = inject(Router);

  trainingProviders: TrainingProvider[] = [];
  courseCatalogue: CourseCatalogueItem[] = [];

  errorMessage = signal<string | null>(null);
  fieldErrors = signal<Record<string, string>>({});

  applicationForm = this.formBuilder.group({
    courseSource: ['CATALOGUE', Validators.required],
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
    // LOAD ON TRAINING PROVIDERS
    this.courseApplicationService.getTrainingProviders().subscribe({
      next: (providers) => {
        this.trainingProviders = providers;
        console.log('Training providers:', providers);
      },
      error: (error) => {
        console.error('Failed to load training providers:', error);
      },
    });

    // LOAD ON COURSES IN CATALOGUE
    this.courseApplicationService.getCourseCatalogue().subscribe({
      next: (courses) => {
        this.courseCatalogue = courses;
        console.log('Course catalogue:', courses);
      },
      error: (error) => {
        console.error('Failed to load courses catalogue:', error);
      },
    });

    // CHECK EMPLOYEE ENTER MANUAL COURSE OR FROM CATALOGUE
    this.applicationForm.get('courseCatalogueItemId')?.valueChanges.subscribe((courseId) => {
      if (courseId == null) {
        return;
      }

      const selectedCourse = this.courseCatalogue.find((course) => course.courseId === courseId);

      if (!selectedCourse) {
        return;
      }

      this.applicationForm.patchValue({
        courseTitle: selectedCourse.title,
        category: selectedCourse.category,
        trainingProviderId: selectedCourse.providerId,
      });
    });

    // CHECK ON COURSE SOURCE
    this.applicationForm.get('courseSource')?.valueChanges.subscribe((source) => {
      const categoryControl = this.applicationForm.get('category');
      const providerControl = this.applicationForm.get('trainingProviderId');

      if (source === 'CATALOGUE') {
        // CLEAR FOR MANUAL COURSE SELECTION DATA
        this.applicationForm.patchValue({
          courseTitle: '',
          courseCatalogueItemId: null,
          category: '',
          trainingProviderId: 0,
          fee: 0,
          halfDay: false,
        });
        // AUTO STATED FROM CATALOGUE
        categoryControl?.disable();
        providerControl?.disable();
      } else {
        // MANUAL ENTRY
        this.applicationForm.patchValue({
          courseTitle: '',
          courseCatalogueItemId: null,
          category: '',
          trainingProviderId: 0,
          fee: 0,
          halfDay: false,
        });
        // EMPLOYEE NEEDS TO FILL UP THE FORM
        categoryControl?.enable();
        providerControl?.enable();
      }
    });

    this.applicationForm.get('category')?.disable();
    this.applicationForm.get('trainingProviderId')?.disable();

    // CHECK ON CATEGORY
    this.applicationForm.get('category')?.valueChanges.subscribe((category) => {
      const isInternal = category === 'INTERNAL_TRAINING';
      const feeControl = this.applicationForm.get('fee');
      const startDateControl = this.applicationForm.get('startDate');
      const endDateControl = this.applicationForm.get('endDate');

      this.applicationForm.patchValue({
        halfDay: isInternal,
      });

      if (isInternal) {
        feeControl?.setValue(0);
        feeControl?.disable();

        // INTERNAL TRAINING IS A HALF-DAY, SINGLE-DAY APPLICATION
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

    // CHECK ON START DATE
    this.applicationForm.get('startDate')?.valueChanges.subscribe((startDate) => {
      const category = this.applicationForm.get('category')?.value;

      if (category === 'INTERNAL_TRAINING') {
        this.applicationForm.get('endDate')?.setValue(startDate);
      }
    });
  }

  onSubmit(): void {
    this.errorMessage.set(null);
    this.fieldErrors.set({});

    if (this.applicationForm.invalid) {
      this.applicationForm.markAllAsTouched();
      return;
    }

    const formValue = this.applicationForm.getRawValue();

    const request: CourseApplicationRequest = {
      courseTitle: formValue.courseTitle ?? '',
      category: formValue.category ?? '',
      trainingProviderId: formValue.trainingProviderId ?? 0,
      courseCatalogueItemId:
        formValue.courseSource === 'CATALOGUE' ? formValue.courseCatalogueItemId : null,
      startDate: formValue.startDate ?? '',
      endDate: formValue.endDate ?? '',
      fee: formValue.fee ?? 0,
      halfDay: formValue.category === 'INTERNAL_TRAINING',
      justification: formValue.justification ?? '',
      workDissemination: formValue.workDissemination ?? '',
    };

    console.log('Submit request:', request);

    this.courseApplicationService.submitApplication('aliceTheEmployee', request).subscribe({
      next: (application) => {
        console.log('Applicatin submitted successfully:', application);
        this.router.navigate(['/course-history']);
      },
      error: (error) => {
        console.error('Failed to submit application:', error);
        const apiError = error.error as ApiErrorResponse;

        this.errorMessage.set(apiError?.message ?? 'Unable to submit course application.');
        this.fieldErrors.set(apiError?.errors ?? {});
      },
    });
  }

  cancel(): void {
    this.router.navigate(['/course-history']);
  }
}
