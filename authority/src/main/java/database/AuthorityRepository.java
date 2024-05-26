package database;

import entity.Authority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Optional<Authority> findByRole(String role);
    List<Authority> findAll();

    boolean existsByRole(String role);
}
