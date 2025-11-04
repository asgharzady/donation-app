package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.Card;
import com.donation.donation_app.entity.Payment;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.model.payment.PaymentHistoryResponseDTO;
import com.donation.donation_app.model.payment.PaymentRequest;
import com.donation.donation_app.repository.CardRepository;
import com.donation.donation_app.repository.IAMRepository;
import com.donation.donation_app.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	public void doPayment(PaymentRequest paymentRequest){
		Payment payment = new Payment();
		payment.setAmount(paymentRequest.getAmount());
		payment.setEmail(paymentRequest.getEmail());
		payment.setToAccount(paymentRequest.getToAccount());
		payment.setCardNo(paymentRequest.getCard().getCardNo());
		if(doTrx()){
			payment.setStatus("success");
		}
		else{
			payment.setStatus("false");
		}
		paymentRepository.save(payment);
	}

	public List<PaymentHistoryResponseDTO> getHistory(String email){
		List<Payment> list = paymentRepository.getAllByEmail(email);
		List<PaymentHistoryResponseDTO> response =
		list.stream().map(l-> PaymentHistoryResponseDTO.toDTO(l)).toList();

		return response;
	}

	public boolean doTrx(){
		//TODO implement
		return true;
	}
}


