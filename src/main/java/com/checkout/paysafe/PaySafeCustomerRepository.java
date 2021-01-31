package com.checkout.paysafe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaySafeCustomerRepository extends JpaRepository<PaySafeCustomer, Long> {
	PaySafeCustomer findByEmail(String emailId);
}