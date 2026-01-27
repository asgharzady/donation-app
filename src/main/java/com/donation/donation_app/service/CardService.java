package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.Card;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.repository.CardRepository;
import com.donation.donation_app.repository.IAMRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CardService {

	private final IAMRepository iamRepository;
	private final CardRepository cardRepository;

	public CardService(IAMRepository iamRepository, CardRepository cardRepository) {
		this.iamRepository = iamRepository;
		this.cardRepository = cardRepository;
	}

	public void saveCard(CardSaveReqDTO request) {
		if (!iamRepository.existsByPhoneNo(request.getPhoneNo())) {
			throw new CustomException("phone number not found");
		}

		Card card = new Card();
		card.setCardNo(request.getCardNo());
		card.setName(request.getName());
		card.setExpDate(request.getExpDate());
		card.setCvv(request.getCvv());
		card.setPhoneNo(request.getPhoneNo());

		cardRepository.save(card);
	}

	public List<CardDTO> getCards(String phoneNo){
		ArrayList<Card> cards = cardRepository.findAllByPhoneNo(phoneNo);
		return cards.stream().map(c -> c.toDto(c)).toList();


	}
}


//TODO card enc
