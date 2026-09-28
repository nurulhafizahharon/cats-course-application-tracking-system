package sg.edu.nus.cats.service.holiday;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class SingaporeHolidayProvider implements HolidayProvider {
	
	private final Set<LocalDate> holidays = Set.of(
			LocalDate.of(2026, 10, 21)
			);

	@Override
	public boolean isHoliday(LocalDate date) {
		// TODO Auto-generated method stub
		return holidays.contains(date);
	}

}
