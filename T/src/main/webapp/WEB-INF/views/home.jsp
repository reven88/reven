<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<H1>Shopping Cart</H1>
<form method=post action="/product/secure/initiatepayment">
<table>
<tr>
<td align="right">Product Name:</td><td><input type="text" name="prodname" value="Car"></td>
</tr>
<tr>
<td align="right">Product Amount:$</td><td><input type="text" name="prodamount" value="23.01"></td><td>USD</td>
</tr>
<tr>
<td align="center" colspan="2"><input type="image" src="https://www.paypal.com/en_US/i/btn/btn_xpressCheckout.gif" align="left" style="margin-right:7px;" name="submit"></td>
</tr>
</table>

</form>


</body>
</html>
