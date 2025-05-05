package s25415.sri_02.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto extends RepresentationModel<PersonDto> {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @JsonBackReference("director")
    private List<MovieDto> directedMovies;

    @JsonBackReference("actor")
    private List<MovieDto> movieRoles;
}