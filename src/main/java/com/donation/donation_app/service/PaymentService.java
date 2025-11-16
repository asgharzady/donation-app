package com.donation.donation_app.service;

import com.donation.donation_app.Exception.CustomException;
import com.donation.donation_app.entity.Card;
import com.donation.donation_app.entity.IAM;
import com.donation.donation_app.entity.Payment;
import com.donation.donation_app.model.card.CardDTO;
import com.donation.donation_app.model.card.CardSaveReqDTO;
import com.donation.donation_app.model.payment.PaymentHistoryResponseDTO;
import com.donation.donation_app.model.payment.PaymentRequest;
import com.donation.donation_app.model.payment.PaymentStatisticsDTO;
import com.donation.donation_app.repository.CardRepository;
import com.donation.donation_app.repository.IAMRepository;
import com.donation.donation_app.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final IAMRepository iamRepository;

	public PaymentService(PaymentRepository paymentRepository, IAMRepository iamRepository) {
		this.paymentRepository = paymentRepository;
		this.iamRepository = iamRepository;
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

	public PaymentStatisticsDTO getStatistics(String email){
		PaymentStatisticsDTO response = new PaymentStatisticsDTO();
		IAM iam = iamRepository.findByEmail(email);
		if(iam == null){
			throw new CustomException("user now found");
		}
		List<Payment> list = paymentRepository.getAllByEmail(email);
		long totalSuccess = list.stream()
				.filter(p -> "success".equalsIgnoreCase(p.getStatus()))
				.count();
		ZoneId zone = ZoneId.systemDefault();

		Instant createdAt = iam.getCreatedAt();
		LocalDate createdDate = createdAt.atZone(zone).toLocalDate();
		LocalDate today = LocalDate.now(zone);

		long monthsPassed = ChronoUnit.MONTHS.between(createdDate, today)+1;
		long monthyDonations = totalSuccess/monthsPassed;

		response.setCompletedDonations(totalSuccess);
		response.setTotalDonation(totalSuccess);
		response.setMonthlyDonations(monthyDonations);
		response.setPendingDonations(0L);

		return response;
	}


	public boolean doTrx(){
		//TODO implement
		return true;
	}
}


