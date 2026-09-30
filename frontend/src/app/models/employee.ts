export interface Employee {

  employeeId: number;
  username: string;
  name: string;
  dateOfJoining: string;
  annualTrainingBudget: number | null;
  trainingDayEntitlement: number | null;
  designation: string;
  active: boolean;
  roles: string[];
  managerId: number | null;
  managerName: string | null;
}
