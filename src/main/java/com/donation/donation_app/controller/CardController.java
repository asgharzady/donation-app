package com.donation.donation_app.controller;

import com.donation.donation_app.model.ResponseDTO;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.service.CardService;
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
		log.info("Save card request for email: " + request.getEmail());
		cardService.saveCard(request);
		log.info("Card saved for email: " + request.getEmail());
		return ResponseEntity.ok(new ResponseDTO("card saved"));
	}

	@GetMapping("/get/{email}")
	public ResponseEntity<List<CardDTO>> getCards(@PathVariable String email) {
		log.info("get card request for email: " + email);
		List<CardDTO> cards = cardService.getCards(email);
		return ResponseEntity.ok(cards);

	}
}


