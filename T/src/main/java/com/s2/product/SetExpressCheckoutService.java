package com.s2.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.ServletContextResource;

import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AddressType;
import urn.ebay.apis.eBLBaseComponents.CountryCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.SellerDetailsType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

@Service
public class SetExpressCheckoutService {
	
	private static String returnURL = "https://localhost:8443/product/secure/returncallback";
	private static String cancelURL = "https://localhost:8443/product/secure/cancelcallback";
	
	private Logger logger = Logger.getLogger(this.getClass().toString());
	
	
	public SetExpressCheckoutReq setExpressCheckoutPrep(String prodname, String prodamount) throws IOException {
		
		SetExpressCheckoutRequestDetailsType setExpressCheckoutRequestDetails = new SetExpressCheckoutRequestDetailsType();
		setExpressCheckoutRequestDetails.setReturnURL(returnURL);
		setExpressCheckoutRequestDetails.setCancelURL(cancelURL);

		
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
		
		PaymentDetailsType paymentDetails1 = new PaymentDetailsType();
		
		BasicAmountType orderTotal = new BasicAmountType(CurrencyCodeType.USD, prodamount);
//		BasicAmountType itemTotal = new BasicAmountType(CurrencyCodeType.USD,"23.00");
//		BasicAmountType shippingTotal = new BasicAmountType(CurrencyCodeType.USD,"1.50");
		
//		paymentDetails1.setItemTotal(itemTotal);
//		paymentDetails1.setShippingTotal(shippingTotal);
		paymentDetails1.setOrderTotal(orderTotal);
		paymentDetails1.setOrderDescription(prodname);
		paymentDetails1.setPaymentAction(PaymentActionCodeType.SALE);
		
		SellerDetailsType sellerDetails1 = new SellerDetailsType();
		sellerDetails1.setPayPalAccountID("reven88@163.com");

		paymentDetails1.setSellerDetails(sellerDetails1);
		paymentDetails1.setPaymentRequestID("PaymentRequest1");

		AddressType shipToAddress1 = new AddressType();
		shipToAddress1.setStreet1("Shanghai");
		shipToAddress1.setCityName("Shanghai");
		shipToAddress1.setStateOrProvince("SH");
		shipToAddress1.setCountry(CountryCodeType.CN);
		shipToAddress1.setPostalCode("123456");

		paymentDetails1.setShipToAddress(shipToAddress1);

		paymentDetailsList.add(paymentDetails1);
	
		setExpressCheckoutRequestDetails.setPaymentDetails(paymentDetailsList);
		SetExpressCheckoutReq setExpressCheckoutReq = new SetExpressCheckoutReq();
		SetExpressCheckoutRequestType setExpressCheckoutRequest = new SetExpressCheckoutRequestType(setExpressCheckoutRequestDetails);
		setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequest);
		
		return setExpressCheckoutReq;
	}
	
	
	public SetExpressCheckoutResponseType setExpressCheckoutInvoke(Resource sdkResource, SetExpressCheckoutReq setExpressCheckoutReq) throws IOException {
		PayPalAPIInterfaceServiceService service = null;

		Properties props = PropertiesLoaderUtils.loadProperties(sdkResource);
		service = new PayPalAPIInterfaceServiceService(props);
		
		SetExpressCheckoutResponseType setExpressCheckoutResponse = null;
		
		try {			
			setExpressCheckoutResponse = service.setExpressCheckout(setExpressCheckoutReq);		
			} catch (Exception e) {			
				logger.severe("Error Message : " + e.getMessage());		
			}

			if (setExpressCheckoutResponse.getAck().getValue().equalsIgnoreCase("success")) {
		    	logger.info("EC Token:" + setExpressCheckoutResponse.getToken());
			}else {			
				List<ErrorType> errorList = setExpressCheckoutResponse.getErrors();			
				logger.severe("API Error Message : "+ errorList.get(0).getLongMessage());		
			}

		return setExpressCheckoutResponse;
		
	}
	
	public SetExpressCheckoutResponseType setExpressCheckoutExecute(Resource sdkResource, String prodname, String prodamount) throws IOException {
		SetExpressCheckoutReq req = setExpressCheckoutPrep(prodname, prodamount);
		SetExpressCheckoutResponseType setExpressCheckoutResponse = setExpressCheckoutInvoke(sdkResource, req);
		return setExpressCheckoutResponse;
	}
		
}