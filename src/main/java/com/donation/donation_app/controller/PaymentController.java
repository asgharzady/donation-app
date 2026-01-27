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
	public ResponseEntity<PaymentHistoryResponseDTO> save(@Validated @RequestBody PaymentRequest request) {
		log.info("payment request for phoneNo: " + request.getPhoneNo());
		String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
		if (tokenPhoneNo == null || !tokenPhoneNo.equals(request.getPhoneNo())) {
			throw new CustomException("Unauthorized: wrong token");
		}
		PaymentHistoryResponseDTO response = paymentService.doPayment(request);
		log.info("Payment processed for phoneNo: " + request.getPhoneNo());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-history/{phoneNo}")
	public ResponseEntity<List<PaymentHistoryResponseDTO>> getHistroy(@PathVariable String phoneNo) {
		log.info("get history request for phoneNo: " + phoneNo);
		String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
		if (tokenPhoneNo == null || !tokenPhoneNo.equals(phoneNo)) {
			throw new CustomException("Unauthorized: wrong token");
		}
		List<PaymentHistoryResponseDTO> response = paymentService.getHistory(phoneNo);
		log.info("return history for phoneNo: " + phoneNo);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-statistics/{phoneNo}")
	public ResponseEntity<PaymentStatisticsDTO> getStats(@PathVariable String phoneNo) {
		log.info("get stats request for phoneNo: " + phoneNo);
		String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
		if (tokenPhoneNo == null || !tokenPhoneNo.equals(phoneNo)) {
			throw new CustomException("Unauthorized: wrong token");
		}
		PaymentStatisticsDTO response = paymentService.getStatistics(phoneNo);
		log.info("return stats for phoneNo: " + phoneNo);
		return ResponseEntity.ok(response);
	}


}
