import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UpdateCourseApplication } from './update-course-application';

describe('UpdateCourseApplication', () => {
  let component: UpdateCourseApplication;
  let fixture: ComponentFixture<UpdateCourseApplication>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateCourseApplication],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateCourseApplication);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
