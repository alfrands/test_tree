package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceCardholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceCardHolderRepository extends JpaRepository<LogServiceCardholder, Long> {
}
