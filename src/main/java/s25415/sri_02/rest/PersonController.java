package s25415.sri_02.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import s25415.sri_02.dto.PersonDto;
import s25415.sri_02.model.Person;
import s25415.sri_02.repo.MovieRepository;
import s25415.sri_02.repo.PeopleRepository;
import s25415.sri_02.validator.PersonDtoValidator;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    private MovieRepository movieRepository;
    private PeopleRepository peopleRepository;
    private ModelMapper modelMapper;

    @Autowired
    private PersonDtoValidator personDtoValidator;

    public PersonController(MovieRepository movieRepository, PeopleRepository peopleRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.peopleRepository = peopleRepository;
        this.modelMapper = modelMapper;
    }

    private PersonDto convertToDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }

    private Person convertToEntity(PersonDto dto) {
        return modelMapper.map(dto, Person.class);
    }

    @GetMapping
    public ResponseEntity<List<PersonDto>> getPeople() {
        List<Person> allPeople = peopleRepository.findAll();

        List<PersonDto> result = allPeople.stream()
                .map(person -> {
                    PersonDto dto = convertToDto(person);

                    dto.add(linkTo(methodOn(PersonController.class).getPersonById(dto.getId())).withSelfRel());
                    dto.add(linkTo(methodOn(PersonController.class).getPeople()).withRel("all-people"));

                    return dto;
                })
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{personId}")
    public ResponseEntity<PersonDto> getPersonById(@PathVariable Long personId) {
        Optional<Person> person = peopleRepository.findById(personId);

        return person.map(p -> {
            PersonDto dto = convertToDto(p);

            dto.add(linkTo(methodOn(PersonController.class).getPersonById(personId)).withSelfRel());
            dto.add(linkTo(methodOn(PersonController.class).getPeople()).withRel("all-people"));

            return new ResponseEntity<>(dto, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity savePerson(@RequestBody PersonDto personDto, BindingResult bindingResult) {
        personDtoValidator.validate(personDto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Person person = convertToEntity(personDto);
        Person savedPerson = peopleRepository.save(person);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{personId}")
                .buildAndExpand(savedPerson.getId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", location.toString());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{personId}")
    public ResponseEntity updatePerson(@PathVariable Long personId, @RequestBody PersonDto personDto, BindingResult bindingResult) {
        personDtoValidator.validate(personDto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err ->
                    errors.put(err.getField(), err.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Optional<Person> person = peopleRepository.findById(personId);
        if (person.isPresent()) {
            personDto.setId(personId);
            Person entity = convertToEntity(personDto);
            peopleRepository.save(entity);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{personId}")
    public ResponseEntity deletePerson(@PathVariable Long personId) {
        Optional<Person> person = peopleRepository.findById(personId);
        if (person.isPresent()) {
            peopleRepository.deleteById(personId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
