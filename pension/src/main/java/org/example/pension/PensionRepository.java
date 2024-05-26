package org.example.pension;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PensionRepository extends CrudRepository<Pension, Long> {
  Optional<Pension> findByEmail(String email);
  List<Pension> findAll();
}
