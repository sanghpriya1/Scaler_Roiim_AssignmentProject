package com.checkout.paysafe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PaySafeCheckoutController {
	
	@Autowired
	private PaySafeCustomerRepository paySafeCustomerRepository;
	
	@Autowired
	private PaySafeCheckoutService paySafeCheckoutService;
	 
	 public PaySafeCheckoutController() {
		
	}
	
	@GetMapping("/SingleUseCustomerToken/{emailId}")
	public SingleUseCustomerTokenDTO getSingleUseCustomerToken(@PathVariable String emailId) {
		
		SingleUseCustomerTokenDTO<SingleUseCustomerTokenResponseDTO> customerTokenDTO = 
				new SingleUseCustomerTokenDTO<SingleUseCustomerTokenResponseDTO>();
		customerTokenDTO.setStatus(HttpStatus.OK);
		customerTokenDTO.setMessage( "SingleUseCustomerToken Created" );
		customerTokenDTO.setData( paySafeCheckoutService.creatSingleUserCustomerToken( emailId ) );
		return customerTokenDTO;
	}
}
