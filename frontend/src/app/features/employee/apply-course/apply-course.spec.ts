import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApplyCourse } from './apply-course';

describe('ApplyCourse', () => {
  let component: ApplyCourse;
  let fixture: ComponentFixture<ApplyCourse>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApplyCourse],
    }).compileComponents();

    fixture = TestBed.createComponent(ApplyCourse);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
