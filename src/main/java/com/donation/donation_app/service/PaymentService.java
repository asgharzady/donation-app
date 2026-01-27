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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IAMRepository iamRepository;

    @Value("${nmi.security.key}")
    private String nmiSecurityKey;

    @Value("${nmi.base.url:${NMI_BASE_URL:https://secure.nmi.com/api/transact.php}}")
    private String nmiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentService(PaymentRepository paymentRepository, IAMRepository iamRepository) {
        this.paymentRepository = paymentRepository;
        this.iamRepository = iamRepository;
    }

    public String doPayment(PaymentRequest paymentRequest){
        Payment payment = new Payment();
        payment.setAmount(paymentRequest.getAmount());
        payment.setPhoneNo(paymentRequest.getPhoneNo());
        payment.setToAccount(paymentRequest.getToAccount());
        payment.setCardNo(paymentRequest.getCard().getCardNo());
        String status = doTrx(paymentRequest);
        if(status.equals("SUCCESS")){
            payment.setStatus("success");
        }
        else{
            payment.setStatus("false");
        }
        paymentRepository.save(payment);

        return status;
    }

    public List<PaymentHistoryResponseDTO> getHistory(String phoneNo){
        List<Payment> list = paymentRepository.getAllByPhoneNo(phoneNo);
        List<PaymentHistoryResponseDTO> response =
        list.stream().map(l-> PaymentHistoryResponseDTO.toDTO(l)).toList();

        return response;
    }

    public PaymentStatisticsDTO getStatistics(String phoneNo){
        PaymentStatisticsDTO response = new PaymentStatisticsDTO();
        IAM iam = iamRepository.findByPhoneNo(phoneNo);
        if(iam == null){
            throw new CustomException("user not found");
        }
        List<Payment> list = paymentRepository.getAllByPhoneNo(phoneNo);
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


    public String doTrx(PaymentRequest paymentRequest){

            if (paymentRequest == null) {
                return "FAILED";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("ccnumber", paymentRequest.getCard().getCardNo());
            form.add("amount", String.valueOf(paymentRequest.getAmount()));
            form.add("cvv", paymentRequest.getCard().getCvv());
            form.add("ccexp", paymentRequest.getCard().getExpDate());
            form.add("type", "sale");
            form.add("security_key", nmiSecurityKey);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

            String body;
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(nmiBaseUrl, request, String.class);
                body = response.getBody();
            } catch (Exception ex) {
                // on error treat as failed transaction
                return "FAILED";
            }

            if (body == null || body.isBlank()) {
                return "FAILED";
            }

            // parse response like: key1=val1&key2=val2...
            Map<String, String> respMap = new HashMap<>();
            String[] pairs = body.split("&");
            for (String p : pairs) {
                int idx = p.indexOf('=');
                if (idx > 0) {
                    String k = URLDecoder.decode(p.substring(0, idx), StandardCharsets.UTF_8);
                    String v = URLDecoder.decode(p.substring(idx + 1), StandardCharsets.UTF_8);
                    respMap.put(k, v);
                }
            }

            String respFlag = respMap.getOrDefault("response", "");
            String respCode = respMap.getOrDefault("response_code", "");
            String respText = respMap.getOrDefault("responsetext", "");

//            return "1".equals(respFlag) || "100".equals(respCode) || respText.toUpperCase().contains("SUCCESS");
            return respText;

    }
}
