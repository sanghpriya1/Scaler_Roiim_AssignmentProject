package com.checkout.paysafe;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PaySafeCheckoutService {
	private PaySafeCustomerRepository paySafeCustomerRepository;
	
	private RestTemplate restTemplate;
	
	private String baseUrl;
	
	private String APIKeyId;

    private String APIKeyPassword;

    private Random random;
    
    public PaySafeCheckoutService() {}
    
    @Autowired
    public PaySafeCheckoutService(PaySafeCustomerRepository paySafeCustomerRepository, RestTemplate restTemplate) {
    	this.paySafeCustomerRepository = paySafeCustomerRepository;
		this.restTemplate = restTemplate;
		baseUrl = "https://api.test.paysafe.com/paymenthub/v1";
        APIKeyId = "private-7751";
        APIKeyPassword = "B-qa2-0-5f031cdd-0-302d0214496be84732a01f690268d3b8eb72e5b8ccf94e2202150085913117f2e1a8531505ee8ccfc8e98df3cf1748";
        random = new Random();
    }

	
		
    public SingleUseCustomerTokenResponseDTO creatSingleUserCustomerToken( String emailId ){

    	PaySafeCustomer paySafeCustomer = null;

        paySafeCustomer = paySafeCustomerRepository.findByEmail( emailId );

        // check if user is previously registered
        if( paySafeCustomer == null ){

            // create a new user
            paySafeCustomer = createpaySafeCustomer( emailId );

            // check if user is created or not
            if( paySafeCustomer == null ){

                return null;
            }
        }

        // get API url in the string
        String url = baseUrl + "/customers/" + paySafeCustomer.getPaysafeRecordId() + "/singleusecustomertokens" ;

        // create new http header and set content type to application/json
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // set basic authorization with api key and its value
        header.setBasicAuth( APIKeyId, APIKeyPassword );

        // create request object
        SingleUseCustomerTokenRequestDTO singleUseCustomerTokenRequestDTO = new SingleUseCustomerTokenRequestDTO();
        singleUseCustomerTokenRequestDTO.setMerchantRefNum( String.valueOf( random.nextInt() ) );
        singleUseCustomerTokenRequestDTO.setPaymentTypes( Arrays.asList( "CARD" ) );

        // convert request object in to json object
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {

            jsonString = objectMapper.writeValueAsString( singleUseCustomerTokenRequestDTO );
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // build the request
        HttpEntity< String > entity = new HttpEntity<>( jsonString, header );

        // send POST request
        ResponseEntity< SingleUseCustomerTokenResponseDTO > responseEntity = restTemplate.postForEntity( url, entity, SingleUseCustomerTokenResponseDTO.class );

        if( responseEntity.getStatusCode().equals( HttpStatus.CREATED ) ) {


            SingleUseCustomerTokenResponseDTO singleUseCustomerTokenResponseDTO = responseEntity.getBody();
            singleUseCustomerTokenResponseDTO.setMerchantRefNum(singleUseCustomerTokenRequestDTO.getMerchantRefNum());
            System.out.println(responseEntity.getBody());
            return responseEntity.getBody();
        }

        return null;
    }

    public PaySafeCustomer createpaySafeCustomer( String emailId ){

        // get API url in the string
        String url = baseUrl + "/customers";

        // create new http header and set content type to application/json
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // set basic authorization with api key and its value
        header.setBasicAuth( APIKeyId, APIKeyPassword );

        // create a new map for the body of the request and put all the values received from the user in the map
        CreateNewCustomerRequestDTO createNewCustomerRequestDTO = new CreateNewCustomerRequestDTO();
        createNewCustomerRequestDTO.setMerchantCustomerId( String.valueOf( random.nextInt() ) );
        createNewCustomerRequestDTO.setLocale( "en_US" );
        createNewCustomerRequestDTO.setFirstName( "Jhonny" );
        createNewCustomerRequestDTO.setMiddleName( "Jon" );
        createNewCustomerRequestDTO.setLastName( "Smith" );
        DateOfBirth dob = new DateOfBirth( 2, 3, 1998 );
        createNewCustomerRequestDTO.setDateOfBirth( dob );
        createNewCustomerRequestDTO.setEmail( emailId );
        createNewCustomerRequestDTO.setCellPhone( "9056482124" );
        createNewCustomerRequestDTO.setGender( "M" );
        createNewCustomerRequestDTO.setNationality( "Canadian" );
        createNewCustomerRequestDTO.setPhone( "777-444-8888" );
        createNewCustomerRequestDTO.setIp( "192.0.126.111" );

        // convert request object in to json object
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = null;
        try {

            jsonString = objectMapper.writeValueAsString(createNewCustomerRequestDTO);
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println( jsonString );

        // build the request
        HttpEntity< String > entity = new HttpEntity< String >( jsonString, header );

        // send POST request
        ResponseEntity< CreateNewCustomerResponseDTO > responseEntity = restTemplate.postForEntity( url, entity, CreateNewCustomerResponseDTO.class );

        // check if user is successfully created
        if( responseEntity.getStatusCode() == HttpStatus.CREATED ){

            // get the response
            CreateNewCustomerResponseDTO response = responseEntity.getBody();

            // create new record for the customer in local database and set it's attributes values
            PaySafeCustomer newCustomer = new PaySafeCustomer();
            newCustomer.setPaysafeRecordId( response.getId() );
            newCustomer.setEmail( response.getEmail() );

            paySafeCustomerRepository.save( newCustomer );
            return newCustomer;
        }
        else {

            System.out.println( "failed user creation" );

            PaySafeCustomer c = null;
            return c;
        }

    }


    
    
    
		 
	
}
