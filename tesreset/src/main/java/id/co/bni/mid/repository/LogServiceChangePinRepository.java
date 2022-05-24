package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceChangePin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceChangePinRepository extends JpaRepository<LogServiceChangePin, Long> {
}
