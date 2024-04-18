package account.payment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    boolean existsByEmployeeAndPeriod(String employee, String period);
    Optional<Payment> findByEmployeeAndPeriod(String employee, String period);
    List<Payment> findByEmployeeOrderByPeriodDesc(String employee);


}

