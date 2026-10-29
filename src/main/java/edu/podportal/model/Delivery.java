package edu.podportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(
    name = "deliveries",
    uniqueConstraints = @UniqueConstraint(columnNames = "tracking_no")
)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tracking number is required")
    @Column(name = "tracking_no", nullable = false, unique = true)
    private String trackingNo;

    @NotBlank(message = "Customer is required")
    private String customer;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Item is required")
    private String item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "assigned_agent")
    private String assignedAgent;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "proof_note", length = 1000)
    private String proofNote;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "otp_required")
    private Boolean otpRequired = false;

    @Column(name = "admin_otp", length = 10)
    private String adminOtp;

    @Column(name = "otp_hashed", length = 100)
    private String otpHashed;

    @Column(name = "otp_expiry")
    private java.time.LocalDateTime otpExpiry;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_status")
    private OtpStatus otpStatus = OtpStatus.NONE;

    @Column(name = "otp_attempts")
    private Integer otpAttempts = 0;

    @Column(name = "entered_otp", length = 20)
    private String enteredOtp;

    @Column(name = "otp_verified_at")
    private java.time.LocalDateTime otpVerifiedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getProofNote() {
        return proofNote;
    }

    public void setProofNote(String proofNote) {
        this.proofNote = proofNote;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Boolean getOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(Boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public String getAdminOtp() {
        return adminOtp;
    }

    public void setAdminOtp(String adminOtp) {
        this.adminOtp = adminOtp;
    }

    public String getOtpHashed() {
        return otpHashed;
    }

    public void setOtpHashed(String otpHashed) {
        this.otpHashed = otpHashed;
    }

    public java.time.LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(java.time.LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public OtpStatus getOtpStatus() {
        return otpStatus;
    }

    public void setOtpStatus(OtpStatus otpStatus) {
        this.otpStatus = otpStatus;
    }

    public Integer getOtpAttempts() {
        return otpAttempts;
    }

    public void setOtpAttempts(Integer otpAttempts) {
        this.otpAttempts = otpAttempts;
    }

    public String getEnteredOtp() {
        return enteredOtp;
    }

    public void setEnteredOtp(String enteredOtp) {
        this.enteredOtp = enteredOtp;
    }

    public java.time.LocalDateTime getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public void setOtpVerifiedAt(java.time.LocalDateTime otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }
}