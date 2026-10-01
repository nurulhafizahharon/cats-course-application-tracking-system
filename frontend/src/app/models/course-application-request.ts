export interface CourseApplicationRequest {
  courseTitle: string;
  category: string;
  trainingProviderId: number;
  courseCatalogueItemId: number | null;
  startDate: string;
  endDate: string;
  fee: number;
  halfDay: boolean;
  justification: string;
  workDissemination: string;
}
