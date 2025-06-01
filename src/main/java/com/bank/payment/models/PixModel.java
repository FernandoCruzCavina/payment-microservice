package com.bank.payment.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_PIXS")
public class PixModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long idPix;

    @Column(name = "pix_key", nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long lastUpdatedAt;

    @Column(nullable = false)
    private Long idAccount;

}
