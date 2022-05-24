package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceInstantCard;
import id.co.bni.mid.model.LogServiceOaslogA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceInstantCardRepository extends JpaRepository<LogServiceInstantCard, Long> {
}
