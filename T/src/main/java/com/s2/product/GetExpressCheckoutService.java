package com.s2.product;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.apis.eBLBaseComponents.ErrorType;

@Service
public class GetExpressCheckoutService {

	private Logger logger = Logger.getLogger(this.getClass().toString());
	
	
	
	public GetExpressCheckoutDetailsResponseType getExpressCheckoutInvoke(Resource sdkResource, String token){
		
		GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
		GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType(token);
		getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);
		PayPalAPIInterfaceServiceService service = null;
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(sdkResource);
			service = new PayPalAPIInterfaceServiceService(props);
		} catch (IOException e) {
			logger.severe("Error Message : " + e.getMessage());
		}
		
		GetExpressCheckoutDetailsResponseType getExpressCheckoutDetailsResponse = null;
		try {
			getExpressCheckoutDetailsResponse = service.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
		} catch (Exception e) {
			logger.severe("Error Message : " + e.getMessage());
		}
		if (getExpressCheckoutDetailsResponse.getAck().getValue().equalsIgnoreCase("success")) {
			logger.info("PayerID : "+ getExpressCheckoutDetailsResponse.getGetExpressCheckoutDetailsResponseDetails()
							.getPayerInfo().getPayerID());
		} else {
			List<ErrorType> errorList = getExpressCheckoutDetailsResponse.getErrors();
			logger.severe("API Error Message : "+ errorList.get(0).getLongMessage());
		}
		return getExpressCheckoutDetailsResponse;
	}
	

}
