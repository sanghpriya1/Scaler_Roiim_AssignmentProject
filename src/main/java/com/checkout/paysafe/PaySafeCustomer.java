package com.checkout.paysafe;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PaySafeCustomer {
	
	@Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private String email;

    private String paysafeRecordId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPaysafeRecordId() {
		return paysafeRecordId;
	}

	public void setPaysafeRecordId(String paysafeRecordId) {
		this.paysafeRecordId = paysafeRecordId;
	}
    
}
