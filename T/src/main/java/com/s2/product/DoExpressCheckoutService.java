package com.s2.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.SellerDetailsType;

@Service
public class DoExpressCheckoutService {
	
	Logger logger = Logger.getLogger(this.getClass().toString());
	
	
	public DoExpressCheckoutPaymentReq doExpressCheckoutPrep(String token, String prodamount, String payerID) {
		DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
		DoExpressCheckoutPaymentRequestDetailsType doExpressCheckoutPaymentRequestDetails = new DoExpressCheckoutPaymentRequestDetailsType();

		doExpressCheckoutPaymentRequestDetails.setToken(token);
		doExpressCheckoutPaymentRequestDetails.setPayerID(payerID);
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();

		PaymentDetailsType paymentDetails1 = new PaymentDetailsType();

		BasicAmountType orderTotal1 = new BasicAmountType(CurrencyCodeType.USD, prodamount);
		paymentDetails1.setOrderTotal(orderTotal1);

		paymentDetails1.setPaymentAction(PaymentActionCodeType.SALE);

		SellerDetailsType sellerDetails1 = new SellerDetailsType();
		sellerDetails1.setPayPalAccountID("reven88@163.com");
		paymentDetails1.setSellerDetails(sellerDetails1);
		paymentDetails1.setPaymentRequestID("PaymentRequest1");

		paymentDetailsList.add(paymentDetails1);

		doExpressCheckoutPaymentRequestDetails.setPaymentDetails(paymentDetailsList);
		DoExpressCheckoutPaymentRequestType doExpressCheckoutPaymentRequest = new DoExpressCheckoutPaymentRequestType(doExpressCheckoutPaymentRequestDetails);
		doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doExpressCheckoutPaymentRequest);
		return doExpressCheckoutPaymentReq;
	}
	
	public Map doExpressCheckoutInvoke(Resource sdkResource, DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq) {
		Map paymentResult = new HashMap();
		String transactionID = "";
		PayPalAPIInterfaceServiceService service = null;
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(sdkResource);
			service = new PayPalAPIInterfaceServiceService(props);
		} catch (IOException e) {
			logger.severe("Error Message : " + e.getMessage());
		}

		DoExpressCheckoutPaymentResponseType doExpressCheckoutPaymentResponse = null;
		try {
			doExpressCheckoutPaymentResponse = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);
		} catch (Exception e) {
			logger.severe("Error Message : " + e.getMessage());
		}

		if (doExpressCheckoutPaymentResponse.getAck().getValue().equalsIgnoreCase("success")) {
			if (doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo() != null) {
				Iterator<PaymentInfoType> paymentInfoIterator = doExpressCheckoutPaymentResponse.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().iterator();
				while (paymentInfoIterator.hasNext()) {
					PaymentInfoType paymentInfo = paymentInfoIterator.next();
					transactionID = paymentInfo.getTransactionID();
					logger.info("Transaction ID : "	+ paymentInfo.getTransactionID());
				}
			}
		} else {
			List<ErrorType> errorList = doExpressCheckoutPaymentResponse.getErrors();
			logger.severe("API Error Message : "+ errorList.get(0).getLongMessage());
			paymentResult.put("errorMessage", errorList.get(0).getLongMessage());
		}
		
		paymentResult.put("status", doExpressCheckoutPaymentResponse.getAck().getValue());
		paymentResult.put("transactionID", transactionID);
		return paymentResult;
	}
	
	public Map doExpressCheckoutExecute(String token, Resource sdkResource, String prodname, String prodamount, String payerID) {
		DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = doExpressCheckoutPrep(token, prodamount, payerID);
		Map paymentResponse = doExpressCheckoutInvoke(sdkResource, doExpressCheckoutPaymentReq);
		
		return paymentResponse;
	}
	

}
