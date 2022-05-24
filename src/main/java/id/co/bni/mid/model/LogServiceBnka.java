package id.co.bni.mid.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bnka_service_log")
@EntityListeners(AuditingEntityListener.class)
public class LogServiceBnka implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    public Long id;

    @Column(name = "request_address")
    private String request_address;

    @Column(name = "traceNbr")
    private String traceNbr;

    @Column(name = "localDateTime")
    public String localDateTime;

    @Column(name = "request")
    public String request;

    @Column(name = "response")
    public String response;

    @Column(name = "createdAt", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(name = "updatedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    public LogServiceBnka() {
    }

    public LogServiceBnka(String request_address, String trxId, String localDateTime, String request) {
        this.request_address = request_address;
        this.traceNbr = trxId;
        this.localDateTime = localDateTime;
        this.request = request;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequest_address() {
        return request_address;
    }

    public void setRequest_address(String request_address) {
        this.request_address = request_address;
    }

    public String getTraceNbr() {
        return traceNbr;
    }

    public void setTraceNbr(String traceNbr) {
        this.traceNbr = traceNbr;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
