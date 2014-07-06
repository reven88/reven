<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<H1>Payment Result</H1>
<tr><td>Payment status:</td><td><c:out value="${paymentstatus}"/></td></tr>
<tr><td>Transaction ID:</td><td><c:out value="${transactionid}"/></td></tr>

</body>
</html>