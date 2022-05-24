package id.co.bni.mid.repository;

import id.co.bni.mid.model.LogServiceInquiry;
import id.co.bni.mid.model.LogServiceValidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
LogServiceInquiryRepository extends JpaRepository<LogServiceInquiry, Long> {
}
