package s25415.sri_02.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto extends RepresentationModel<MovieDto> {
    private Long id;
    private String title;
    private PersonDto director;
    private List<PersonDto> actors;
    private LocalDate releaseDate;
    private String genre;
    private double rating;
    private int durationMinutes;
    private boolean isAvailableForStreaming;
}