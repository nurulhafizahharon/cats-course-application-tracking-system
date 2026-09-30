import { Routes } from '@angular/router';
import { Home } from './features/employee/home/home';
import { Profile } from './features/employee/profile/profile';
import { CourseHistory } from './features/employee/course-history/course-history';
import { ApplyCourse } from './features/employee/apply-course/apply-course';
import { CourseApplicationDetails } from './features/employee/course-application-details/course-application-details';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'home',
    component: Home,
  },
  {
    path: 'profile',
    component: Profile,
  },
  {
    path: 'course-history',
    component: CourseHistory,
  },
  {
    path: 'apply-course',
    component: ApplyCourse,
  },
  {
    path: 'course-history/:applicationId',
    component: CourseApplicationDetails,
  },
];
