import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseApplicationDetails } from './course-application-details';

describe('CourseApplicationDetails', () => {
  let component: CourseApplicationDetails;
  let fixture: ComponentFixture<CourseApplicationDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseApplicationDetails],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseApplicationDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
