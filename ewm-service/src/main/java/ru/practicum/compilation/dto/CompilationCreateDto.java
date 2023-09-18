package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreateDto {
    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    List<Long> events;
}
