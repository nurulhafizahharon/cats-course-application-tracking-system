import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { CourseApplication } from '../models/course-application';
import { Page } from '../models/page';

@Service()
export class CourseApplicationService {
  private http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/applications';

  getApplications(username: string, page: number = 0, size: number = 10) {
    const params = new HttpParams().set('username', username).set('page', page).set('size', size);
    return this.http.get<Page<CourseApplication>>(`${this.apiUrl}`, {
      params,
    });
  }

  getApplication(applicationId: number, username: string) {
    const params = new HttpParams().set('username', username);

    return this.http.get<CourseApplication>(`${this.apiUrl}/${applicationId}`, { params });
  }
}
