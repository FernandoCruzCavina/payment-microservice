package com.bank.payment.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.payment.dtos.PaymentAnalyzeDto;
import com.bank.payment.dtos.PaymentDto;
import com.bank.payment.models.PaymentModel;
import com.bank.payment.services.AccountService;
import com.bank.payment.services.KnownPixService;
import com.bank.payment.services.PaymentService;
import com.bank.payment.services.PixService;

/**
 * REST controller for handling payment operations.
 * <p>
 * Provides endpoints for retrieving, deleting, analyzing, and processing payments via Pix.
 * Delegates business logic to the {@link PaymentService}.
 * </p>
 * 
 * <ul>
 *   <li>GET /{idPayment} - Retrieve a payment by its ID</li>
 *   <li>DELETE /{idPayment} - Delete a payment by its ID</li>
 *   <li>POST /{idAccount}/pix/{pixKey} - Analyze a payment before processing</li>
 *   <li>POST /{idAccount}/pix/{pixKey}/direct - Process a direct payment</li>
 * </ul>
 * 
 * @author Fernando Cruz Cavina
 * @since 1.0.0
 */
@RestController
@RequestMapping()
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    AccountService accountService;

    @Autowired
    KnownPixService knownPixService;

    @Autowired
    PixService pixService;

    /**
     * Retrieves a payment by its ID.
     *
     * @param idPayment the ID of the payment
     * @return a ResponseEntity containing the payment details
     */
    @GetMapping("/{idPayment}")
    public ResponseEntity<Object> getOnePayment(@PathVariable(value = "idPayment") Long idPayment) {
        PaymentModel paymentModel = paymentService.findById(idPayment);
        return ResponseEntity.status(HttpStatus.OK).body(paymentModel);
    }

    /**
     * Deletes a payment by its ID.
     *
     * @param idPayment the ID of the payment to delete
     * @return a ResponseEntity containing a confirmation message
     */
    @DeleteMapping("/{idPayment}")
    public ResponseEntity<Object> deletePayment(@PathVariable(value = "idPayment") Long idPayment) {
        String response = paymentService.delete(idPayment); 
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Analyzes a payment before processing, returning a confirmation message.
     *
     * @param idAccount the ID of the sender's account
     * @param pixKey the Pix key of the receiver
     * @param paymentDto the payment analysis data
     * @return a ResponseEntity containing a confirmation message
     */
    @PostMapping("/{idAccount}/pix/{pixKey}")
    public ResponseEntity<Object> analyzePayment(@PathVariable(value = "idAccount") Long idAccount,
            @PathVariable(value = "pixKey") String pixKey, @RequestBody PaymentAnalyzeDto paymentDto) {
        String response = paymentService.reviewPaymentBeforeProcessing(idAccount, pixKey, paymentDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Processes a direct payment from the sender to the receiver using Pix without any analysis.
     *
     * @param idAccount the ID of the sender's account
     * @param pixKey the Pix key of the receiver
     * @param paymentDto the payment data
     * @return a ResponseEntity containing a confirmation message
     */
    @PostMapping("/{idAccount}/pix/{pixKey}/direct")
    public ResponseEntity<Object> directPayment(@PathVariable(value = "idAccount") Long idAccount,
            @PathVariable(value = "pixKey") String pixKey, @RequestBody @Validated PaymentDto paymentDto) {
        String response = paymentService.directPayment(idAccount, pixKey, paymentDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
