export interface CourseApplication {
  applicationId: number;
  employeeId: number;
  employeeName: string;
  applicationDate: string;

  courseTitle: string;
  category: string;

  trainingProviderId: number;
  trainingProviderName: string;

  courseCatalogueItemId: number | null;

  startDate: string;
  endDate: string;

  durationDays: number;
  fee: number;
  halfDay: boolean;

  justification: string;
  workDissemination: string;

  status: string;

  lastUpdatedAt: string;

  managerReason: string | null;
  decidedById: number | null;
  decidedByName: string | null;
  decisionDate: string | null;

  experienceComment: string | null;
}
