package com.donation.donation_app.controller;

import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.model.payment.PaymentHistoryResponseDTO;
import com.donation.donation_app.model.payment.PaymentRequest;
import com.donation.donation_app.service.CardService;
import com.donation.donation_app.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("payment")
public class PaymentController {

	private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}


	@PostMapping("/send")
	public ResponseEntity<ResponseDTO> save(@Validated @RequestBody PaymentRequest request) {
		log.info("payment request for email: " + request.getEmail());
		paymentService.doPayment(request);
		log.info("Card saved for email: " + request.getEmail());
		return ResponseEntity.ok(new ResponseDTO("success"));
	}

	@GetMapping("/get-history/{email}")
	public ResponseEntity<List<PaymentHistoryResponseDTO>> getHistroy(@PathVariable String email) {
		log.info("get histroy request for email: " + email);
		List<PaymentHistoryResponseDTO> response = paymentService.getHistory(email);
		log.info("return hostry for email: " + email);
		return ResponseEntity.ok(response);
	}


}




