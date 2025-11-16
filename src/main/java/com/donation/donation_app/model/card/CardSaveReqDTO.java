package com.donation.donation_app.model.card;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardSaveReqDTO {

	@NotBlank
	private String cardNo;

	@NotBlank
	private String name;

	@NotBlank
	private String expDate;

	@NotBlank
	private String cvv;

	@Email
	@NotBlank
	private String email;
}





