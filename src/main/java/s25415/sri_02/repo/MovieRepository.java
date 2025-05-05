package s25415.sri_02.repo;


import org.springframework.data.repository.CrudRepository;
import s25415.sri_02.model.Movie;

import java.util.List;

public interface MovieRepository extends CrudRepository<Movie, Long> {
    List<Movie> findAll();
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
