import { TestBed } from '@angular/core/testing';
import { CourseApplicationService } from './course-application-service';

describe('CourseApplicationService', () => {
  let service: CourseApplicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CourseApplicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
