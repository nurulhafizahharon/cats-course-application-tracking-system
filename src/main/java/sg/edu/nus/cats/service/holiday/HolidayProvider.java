package sg.edu.nus.cats.service.holiday;

import java.time.LocalDate;

public interface HolidayProvider {
	
	boolean isHoliday(LocalDate date);

}
