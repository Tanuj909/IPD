package com.ipd.controller;
import com.ipd.entity.IpdBilling;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdBillingRepository;
import com.ipd.entity.IpdAdmission;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.user.entity.Doctor;
import com.user.repository.DoctorRepository;

import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;



@RestController
@RequestMapping("/api/ipd/billing")
public class IpdBillingController {

    @Autowired
    private IpdBillingRepository billingRepo;

    @Autowired
    private IpdAdmissionRepository admissionRepo;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @PostMapping("/create-razorpay-order")
    public ResponseEntity<?> createRazorpayOrder(@RequestParam double amount) {
        try {
            RazorpayClient client = new RazorpayClient("rzp_test_SLocMbdcrOU4MH", "5OWF6SelUGd3Ue637n2CFx82");

            int amountInPaise = (int) (amount * 100); // Razorpay accepts paise

            JSONObject options = new JSONObject();
            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            
            // ✅ Keep it under 40 characters
            String receipt = "RCPT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            options.put("receipt", receipt);

            Order order = client.orders.create(options);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", order.get("amount"));
            response.put("currency", order.get("currency"));

            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    // ✅ Save Razorpay Payment
    @PostMapping("/verify-payment")
    public ResponseEntity<String> verifyAndSavePayment(
            @RequestParam Long admissionId,
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature
    ) throws Exception {

        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));

        String generatedSignature = hmacSHA256(orderId + "|" + paymentId, "YOUR_SECRET_KEY");

        if (!generatedSignature.equals(signature)) {
            throw new IllegalArgumentException("Invalid signature from Razorpay");
        }

        billing.setPaid(true);
        billing.setFinalAmount(0);
        billing.setPaymentMode("ONLINE");
        billing.setRazorpayOrderId(orderId);
        billing.setRazorpayPaymentId(paymentId);
        billing.setRazorpaySignature(signature);
        billing.setPaidAt(LocalDateTime.now());

        billingRepo.save(billing);
        return ResponseEntity.ok("Payment successful and saved.");
    }

    private String hmacSHA256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
    }

    // ✅ Doctor Visit Tracking
    @PutMapping("/doctor-visit/{admissionId}")
    public ResponseEntity<IpdBilling> addDoctorVisit(@PathVariable Long admissionId) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));
        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));
        
        Long doctorId = admission.getDoctorId();
        
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(()->new ResourceNotFoundException("Doctor Not Present with this Admission"));

        LocalDateTime today = LocalDateTime.now();

        billing.setDoctorVisitCount(billing.getDoctorVisitCount() + 1);

        if (billing.getLastVisitDate() == null ||
            !billing.getLastVisitDate().toLocalDate().equals(today.toLocalDate())) {

            double fee = doctor.getConsultationFee();
            billing.setDoctorFee(billing.getDoctorFee() + fee);
            billing.setTotalAmount(billing.getTotalAmount() + fee);
            billing.setFinalAmount(billing.getFinalAmount() + fee);
            billing.setLastVisitDate(today);
        }

        billingRepo.save(billing);
        return ResponseEntity.ok(billing);
    }

    // ✅ Pay by Cash
    @PutMapping("/pay-cash/{admissionId}")
    public ResponseEntity<String> payByCash(@PathVariable Long admissionId) {
        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));

        billing.setPaid(true);
        billing.setPaymentMode("CASH");
        billing.setFinalAmount(0);
        billing.setPaidAt(LocalDateTime.now());

        billingRepo.save(billing);
        return ResponseEntity.ok("Cash payment completed.");
    }

    // ✅ Get Billing Info
    @GetMapping("/by-admission/{admissionId}")
    public ResponseEntity<IpdBilling> getBillingByAdmission(@PathVariable Long admissionId) {
        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));
        return ResponseEntity.ok(billing);
    }
}
