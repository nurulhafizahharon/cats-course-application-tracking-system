import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourseHistory } from './course-history';

describe('CourseHistory', () => {
  let component: CourseHistory;
  let fixture: ComponentFixture<CourseHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseHistory],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
