package database;

import entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
  boolean existsByEmail(String email);
  void deleteByEmail(String email);
  Optional<User> findByEmail(String email);
  List<User> findAll();
}
