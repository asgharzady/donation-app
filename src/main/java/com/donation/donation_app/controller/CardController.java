package com.donation.donation_app.controller;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.service.CardService;
import com.donation.donation_app.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("card")
public class CardController {

	private static final Logger log = LoggerFactory.getLogger(CardController.class);

	private final CardService cardService;

	public CardController(CardService cardService) {
		this.cardService = cardService;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseDTO> save(@Validated @RequestBody CardSaveReqDTO request) {
		log.info("Save card request for phoneNo: " + request.getPhoneNo());
		String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
		if (tokenPhoneNo == null || !tokenPhoneNo.equals(request.getPhoneNo())) {
			throw new CustomException("Unauthorized: wrong token");
		}

		cardService.saveCard(request);
		log.info("Card saved for phoneNo: " + request.getPhoneNo());
		return ResponseEntity.ok(new ResponseDTO("success"));
	}

	@GetMapping("/get/{phoneNo}")
	public ResponseEntity<List<CardDTO>> getCards(@PathVariable String phoneNo) {
		log.info("get card request for phoneNo: " + phoneNo);
		String tokenPhoneNo = JwtUtil.getAuthenticatedPhoneNo();
		if (tokenPhoneNo == null || !tokenPhoneNo.equals(phoneNo)) {
			throw new CustomException("Unauthorized: wrong token");
		}
		List<CardDTO> cards = cardService.getCards(phoneNo);
		return ResponseEntity.ok(cards);

	}
}
