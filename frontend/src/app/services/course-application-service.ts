import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { CourseApplication } from '../models/course-application';
import { Page } from '../models/page';
import { CourseApplicationRequest } from '../models/course-application-request';
import { Observable } from 'rxjs';
import { TrainingProvider } from '../models/training-provider';
import { CourseCatalogueItem } from '../models/course-catalogue-item';

@Service()
export class CourseApplicationService {
  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api';

  // GET ALL APPLICATIONS BY USERNAME
  getApplications(username: string, page: number = 0, size: number = 10) {
    const params = new HttpParams().set('username', username).set('page', page).set('size', size);
    return this.http.get<Page<CourseApplication>>(`${this.apiUrl}/applications`, {
      params,
    });
  }

  // GET 1 APPLICATION BY APPLICATION ID AND USERNAME
  getApplication(applicationId: number, username: string) {
    const params = new HttpParams().set('username', username);

    return this.http.get<CourseApplication>(`${this.apiUrl}/applications/${applicationId}`, {
      params,
    });
  }

  // CHANGE 1 APPLICATION STATUS TO DELETE BY APPLICATION ID AND USERNAME
  deleteApplication(applicationId: number, username: string) {
    const params = new HttpParams().set('username', username);

    return this.http.delete<CourseApplication>(`${this.apiUrl}/applications/${applicationId}`, {
      params,
    });
  }

  // UPDATE 1 APPLICATION BY APPLICATION ID AND USERNAME. CHANGE THE APPLICATION STATUS TO UPDATE
  updateApplication(applicationId: number, username: string, request: CourseApplicationRequest) {
    const params = new HttpParams().set('username', username);

    return this.http.put<CourseApplication>(
      `${this.apiUrl}/applications/${applicationId}`,
      request,
      { params },
    );
  }

  // GET ALL ACTIVE TRAINING PROVIDERS
  getTrainingProviders(): Observable<TrainingProvider[]> {
    return this.http.get<TrainingProvider[]>(`${this.apiUrl}/training-providers`);
  }

  // GET ALL ACTIVE COURSES IN CATALOGUE
  getCourseCatalogue(): Observable<CourseCatalogueItem[]> {
    return this.http.get<CourseCatalogueItem[]>(`${this.apiUrl}/course-catalogue`);
  }

  //SUBMIT NEW COURSE APPLICATION
  submitApplication(
    username: string,
    request: CourseApplicationRequest,
  ): Observable<CourseApplication> {
    const params = new HttpParams().set('username', username);
    return this.http.post<CourseApplication>(`${this.apiUrl}/applications`, request, { params });
  }
}
