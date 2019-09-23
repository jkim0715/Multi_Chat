<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>



<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
<script>
function sendMsg(msg){
	$.ajax({
		url:'chat',
		data:{"msg":msg},
		success:function(data){
			$('#msg').val('');
		}	
	});
	
}

$(document).ready(function(){ 
	$('#bt').click(function(){
		var msg = $('#msg').val();
		sendMsg(msg);
	});	
 }); 

</script>
</head>
<body>
<h1>Chat Web Client</h1>
<!-- <form method="get" action="chat">
<input type="text" name="msg">
<input type="submit"> -->

<input id="msg" type="text" name="msg">
<input id="bt" type="button" value="보내기">

</form>
</body>	
</html>