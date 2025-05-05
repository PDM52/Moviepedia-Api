package s25415.sri_02.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import s25415.sri_02.dto.MovieDto;
import s25415.sri_02.dto.PersonDto;
import s25415.sri_02.model.Movie;
import s25415.sri_02.model.Person;
import s25415.sri_02.repo.MovieRepository;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import s25415.sri_02.repo.PeopleRepository;
import s25415.sri_02.validator.MovieDtoValidator;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieController {
    private MovieRepository movieRepository;
    private PeopleRepository peopleRepository;
    private ModelMapper modelMapper;

    @Autowired
    private MovieDtoValidator movieDtoValidator;

    public MovieController(MovieRepository movieRepository, PeopleRepository peopleRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.peopleRepository = peopleRepository;
        this.modelMapper = modelMapper;
    }

    private MovieDto convertMovieToDto(Movie e) {
        return modelMapper.map(e, MovieDto.class);
    }
    private PersonDto convertPersonToDto(Person e) {
        return modelMapper.map(e, PersonDto.class);
    }

    private Movie convertToEntity(MovieDto dto) {
        return modelMapper.map(dto, Movie.class);
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> getMovies()
    {
        List<Movie> allMovies = movieRepository.findAll();
        List<MovieDto> result = allMovies.stream()
                .map(this::convertMovieToDto)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);

        if (movie.isPresent()) {
            MovieDto result = convertMovieToDto(movie.get());
            result.add(linkTo(methodOn(MovieController.class).getMovieById(movieId)).withSelfRel());

            result.add(linkTo(methodOn(MovieController.class).getMovies()).withRel("all-movies"));

            if (result.getDirector() != null) {
                result.add(linkTo(methodOn(PersonController.class).getPersonById(result.getDirector().getId()))
                        .withRel("director"));
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> getMoviesByTitle(@RequestParam String title) {
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title);
        List<MovieDto> result = movies.stream()
                .map(movie -> {
                    MovieDto dto = convertMovieToDto(movie);

                    dto.add(linkTo(methodOn(MovieController.class).getMovieById(dto.getId())).withSelfRel());
                    dto.add(linkTo(methodOn(MovieController.class).getMovies()).withRel("all-movies"));

                    if (dto.getDirector() != null) {
                        dto.add(linkTo(methodOn(PersonController.class)
                                .getPersonById(dto.getDirector().getId()))
                                .withRel("director"));
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveMovie(@RequestBody MovieDto movieDto, BindingResult bindingResult) {
        movieDtoValidator.validate(movieDto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Movie movie = convertToEntity(movieDto);
        movie.setDirector(peopleRepository.findById(movieDto.getDirector().getId())
                .orElseThrow(() -> new RuntimeException("Director not found")));

        movie.setActors(movieDto.getActors().stream()
                .map(actorDto -> peopleRepository.findById(actorDto.getId())
                        .orElseThrow(() -> new RuntimeException("Actor not found")))
                .collect(Collectors.toList()));


        Movie savedMovie = movieRepository.save(movie);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{movieId}")
                .buildAndExpand(savedMovie.getId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", location.toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{movieId}")
    public ResponseEntity updateMovie(@PathVariable Long movieId, @RequestBody MovieDto movieDto, BindingResult bindingResult) {
        movieDtoValidator.validate(movieDto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Optional<Movie> movie = movieRepository.findById(movieId);
        if(movie.isPresent()){
            movieDto.setId(movieId);
            Movie entity = convertToEntity(movieDto);
            movieRepository.save(entity);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity deleteMovie(@PathVariable Long movieId) {
        Optional<Movie> employee = movieRepository.findById(movieId);
        if(employee.isPresent()){
            movieRepository.deleteById(movieId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<PersonDto>> getActorsForMovie(@PathVariable Long movieId) {
        return movieRepository.findById(movieId)
                .map(movie -> {
                    List<PersonDto> actors = movie.getActors().stream()
                            .map(this::convertPersonToDto)
                            .collect(Collectors.toList());
                    return new ResponseEntity<>(actors, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity addActorToMovie(@PathVariable Long movieId, @PathVariable Long actorId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        Optional<Person> actorOpt = peopleRepository.findById(actorId);

        if (movieOpt.isPresent() && actorOpt.isPresent()) {
            Movie movie = movieOpt.get();
            Person actor = actorOpt.get();
            movie.getActors().add(actor);
            movieRepository.save(movie);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity removeActorFromMovie(@PathVariable Long movieId, @PathVariable Long actorId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        Optional<Person> actorOpt = peopleRepository.findById(actorId);

        if (movieOpt.isPresent() && actorOpt.isPresent()) {
            Movie movie = movieOpt.get();
            Person actor = actorOpt.get();

            if (movie.getActors().remove(actor)) {
                movieRepository.save(movie);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Actor was not assigned to this movie", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
