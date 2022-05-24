package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceCheckTerminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogServiceCheckTerminalRepository extends JpaRepository<LogServiceCheckTerminal, Long> {
}
