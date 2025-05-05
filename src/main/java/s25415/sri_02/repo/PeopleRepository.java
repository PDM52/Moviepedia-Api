package s25415.sri_02.repo;


import org.springframework.data.repository.CrudRepository;
import s25415.sri_02.model.Movie;
import s25415.sri_02.model.Person;

import java.util.List;

public interface PeopleRepository extends CrudRepository<Person, Long> {
    List<Person> findAll();
}
