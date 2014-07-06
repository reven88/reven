<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ page session="false" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Payment Success!</title>
</head>
<body>
<H1>Are you going to complete the payment?</H1>

<form method=post action="/product/secure/executepayment">
<input type="hidden" name="prodname" value="${prodname}">
<input type="hidden" name="prodamount" value="${prodamount}">
<input type="hidden" name="payerID" value="${payerID}">
<table>
<tr>
<td align="right">Product Name:</td><td><c:out value="${prodname}"/></td>
</tr>
<tr>
<td align="right">Product Amount:$</td><td><c:out value="${prodamount}"/>USD</td>
</tr>
<tr>
<td align="right">Your Payer ID is:</td><td><c:out value="${payerID}"/></td>
</tr>
<tr>
<td align="right">The Seller Name is:</td><td><c:out value="${sellerUsername}"/></td>
</tr>
<tr>
<td align="center" colspan="2"><input type="image" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif" align="left" style="margin-right:7px;" name="submit"></td>
</tr>
</table>

</form>

</body>
</html>