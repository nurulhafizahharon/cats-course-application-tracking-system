package sg.edu.nus.cats.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import sg.edu.nus.cats.enums.CourseCategory;
import sg.edu.nus.cats.service.holiday.HolidayProvider;

@Service
public class TrainingDayCalculator {
	
	private final HolidayProvider holidayProvider;
	
	public TrainingDayCalculator(HolidayProvider holidayProvider) {
		this.holidayProvider = holidayProvider;
	}
	
	public BigDecimal calculate(LocalDate startDate, LocalDate endDate, CourseCategory category, boolean halfDay) {
		validateDates(startDate, endDate);
		
		validateHalfDay(startDate, endDate, category, halfDay);
		
		if(halfDay) {
			return new BigDecimal("0.5");
		}
		
		return BigDecimal.valueOf(countWorkingDays(startDate, endDate));
		
	}
	
	private void validateDates(LocalDate startDate, LocalDate endDate) {
		if(startDate == null || endDate == null) {
			throw new IllegalArgumentException("Start date and end date are required");
		}
		if(startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date cannot be after end date");
		}
		if(!startDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Start date must be in the future");
		}
		if(!isWorkingDay(startDate)) {
			throw new IllegalArgumentException("Start date must be a working day");
		}
		if (!isWorkingDay(endDate)) {
		    throw new IllegalArgumentException("End date must be a working day");
		}	
	}
	
	private void validateHalfDay(LocalDate startDate, LocalDate endDate, CourseCategory category, boolean halfDay) {
		if(!halfDay) {
			return;
		}
		
		if(category != CourseCategory.INTERNAL_TRAINING) {
			throw new IllegalArgumentException("Half-day is only allowed for Internal Training");
		}
		
		if(!startDate.equals(endDate)) {
			throw new IllegalArgumentException("Half-day training must be a single-day application.");
		}
	}
	
	private long countWorkingDays(LocalDate startDate, LocalDate endDate) {
		long workingDays = 0;
		LocalDate current = startDate;
		while(!current.isAfter(endDate)) {
			if(isWorkingDay(current)) {
				workingDays++;
			}
			current = current.plusDays(1);
		}
		
		return workingDays;
	}
	
	private boolean isWorkingDay(LocalDate date) {
		DayOfWeek day = date.getDayOfWeek();
		
		boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
		
		boolean holiday = holidayProvider.isHoliday(date);
		
		return !weekend && !holiday;
	}

}
