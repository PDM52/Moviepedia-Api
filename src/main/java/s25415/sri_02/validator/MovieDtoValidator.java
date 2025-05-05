package s25415.sri_02.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import s25415.sri_02.dto.MovieDto;

@Component
public class MovieDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MovieDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MovieDto movie = (MovieDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.empty", "Title is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "genre", "genre.empty", "Genre is required");

        if (movie.getReleaseDate() == null) {
            errors.rejectValue("releaseDate", "releaseDate.null", "Release date is required");
        }

        if (movie.getDurationMinutes() < 1) {
            errors.rejectValue("durationMinutes", "durationMinutes.invalid", "Duration must be at least 1 minute");
        }

        if (movie.getRating() < 0.0 || movie.getRating() > 10.0) {
            errors.rejectValue("rating", "rating.invalid", "Rating must be between 0 and 10");
        }

        if (movie.getDirector() == null || movie.getDirector().getId() == null) {
            errors.rejectValue("director", "director.null", "Director is required");
        }

        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            errors.rejectValue("actors", "actors.empty", "Actors list cannot be empty");
        }
    }
}