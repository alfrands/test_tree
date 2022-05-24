package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceOaai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceOaaiRepository extends JpaRepository<LogServiceOaai, Long> {
}
