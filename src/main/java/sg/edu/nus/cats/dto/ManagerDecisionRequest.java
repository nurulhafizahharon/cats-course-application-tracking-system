package sg.edu.nus.cats.dto;

import jakarta.validation.constraints.NotBlank;

public record ManagerDecisionRequest(@NotBlank(message = "Manager reason is required") String reason) {

}
