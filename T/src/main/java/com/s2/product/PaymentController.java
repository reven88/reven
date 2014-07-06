package com.s2.product;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.ServletContextResource;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.eBLBaseComponents.ErrorType;
import urn.ebay.apis.eBLBaseComponents.GetExpressCheckoutDetailsResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;


@Controller
public class PaymentController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@Autowired
	private SetExpressCheckoutService setECService;
	
	@Autowired
	private GetExpressCheckoutService getECService;
	
	@Autowired
	private DoExpressCheckoutService doECService;
	
	String token;	
	
	private Resource sdkResource;
	
	
	public Resource getSdkResource() {
		return sdkResource;
	}

	public void setSdkResource(Resource sdkResource) {
		this.sdkResource = sdkResource;
	}

	public SetExpressCheckoutService getSetECService(){
		return setECService;
	}
		
	public GetExpressCheckoutService getGetECService(){
		return getECService;
	}
	
	
	public DoExpressCheckoutService getDoECService(){
		return doECService;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
	@RequestMapping(value = "/secure/initiatepayment", method = RequestMethod.POST)
	public String initiatepayment(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String prodname = (String)request.getParameter("prodname");
		String prodamount = (String)request.getParameter("prodamount");
		
		Resource sdkResource = new ServletContextResource(request.getSession().getServletContext(), "/resources/sdk_config.properties");
		this.setSdkResource(sdkResource);
		
		SetExpressCheckoutResponseType paypelresponse = this.getSetECService().setExpressCheckoutExecute(sdkResource, prodname, prodamount);
		
		String token = paypelresponse.getToken();
		
		this.token = token;
		
		return "redirect:https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+token;
	}			
	

	@RequestMapping(value = "/secure/returncallback", method = RequestMethod.GET)
	public String returncallback(HttpServletRequest request, HttpServletResponse response) {

		GetExpressCheckoutDetailsResponseType paypalresponse = this.getGetECService().getExpressCheckoutInvoke(sdkResource, token);

		GetExpressCheckoutDetailsResponseDetailsType responseDetials = paypalresponse.getGetExpressCheckoutDetailsResponseDetails();
		
		
		List<PaymentDetailsType> paymentDetailsList = new ArrayList<PaymentDetailsType>();
		paymentDetailsList = responseDetials.getPaymentDetails();
		PaymentDetailsType paymentDetails = paymentDetailsList.get(0);
		String sellerUsername = paymentDetails.getSellerDetails().getPayPalAccountID();
		
		String payerID = responseDetials.getPayerInfo().getPayerID();
		
		request.setAttribute("prodname", paymentDetails.getOrderDescription());
		request.setAttribute("prodamount", paymentDetails.getOrderTotal().getValue());
		request.setAttribute("payerID", payerID);
		request.setAttribute("sellerUsername", sellerUsername);
		return "receipt";
	}			
	
	
	@RequestMapping(value = "/secure/executepayment", method = RequestMethod.POST)
	public String executepayment(HttpServletRequest request, HttpServletResponse response) {
		String prodname = (String)request.getParameter("prodname");
		String prodamount = (String)request.getParameter("prodamount");
		String payerID = (String)request.getParameter("payerID");
		
		Map paymentResult = this.getDoECService().doExpressCheckoutExecute(token, sdkResource, prodname, prodamount, payerID);

		String paymentstatus = (String)paymentResult.get("status");
		String transactionID = (String)paymentResult.get("transactionID");
		
		
		request.setAttribute("paymentstatus", paymentstatus);
		request.setAttribute("transactionid", transactionID);
		
		
		return "confirmation";
	}		



	
}
