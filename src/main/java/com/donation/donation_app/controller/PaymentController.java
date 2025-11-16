package com.donation.donation_app.controller;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.model.payment.PaymentHistoryResponseDTO;
import com.donation.donation_app.model.payment.PaymentRequest;
import com.donation.donation_app.model.payment.PaymentStatisticsDTO;
import com.donation.donation_app.service.CardService;
import com.donation.donation_app.service.PaymentService;
import com.donation.donation_app.util.JwtUtil;
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
		String tokenEmail = JwtUtil.getAuthenticatedEmail();
		if (tokenEmail == null || !tokenEmail.equals(request.getEmail())) {
			throw new CustomException("Unauthorized: wrong token");
		}
		paymentService.doPayment(request);
		log.info("Card saved for email: " + request.getEmail());
		return ResponseEntity.ok(new ResponseDTO("success"));
	}

	@GetMapping("/get-history/{email}")
	public ResponseEntity<List<PaymentHistoryResponseDTO>> getHistroy(@PathVariable String email) {
		log.info("get history request for email: " + email);
		String tokenEmail = JwtUtil.getAuthenticatedEmail();
		if (tokenEmail == null || !tokenEmail.equals(email)) {
			throw new CustomException("Unauthorized: wrong token");
		}
		List<PaymentHistoryResponseDTO> response = paymentService.getHistory(email);
		log.info("return history for email: " + email);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-statistics/{email}")
	public ResponseEntity<PaymentStatisticsDTO> getStats(@PathVariable String email) {
		log.info("get stats request for email: " + email);
		String tokenEmail = JwtUtil.getAuthenticatedEmail();
		if (tokenEmail == null || !tokenEmail.equals(email)) {
			throw new CustomException("Unauthorized: wrong token");
		}
		PaymentStatisticsDTO response = paymentService.getStatistics(email);
		log.info("return stats for email: " + email);
		return ResponseEntity.ok(response);
	}


}




