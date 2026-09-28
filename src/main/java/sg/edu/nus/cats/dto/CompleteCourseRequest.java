package sg.edu.nus.cats.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteCourseRequest(@NotBlank(message = "Experience comment is required") String experienceComment) {

}
