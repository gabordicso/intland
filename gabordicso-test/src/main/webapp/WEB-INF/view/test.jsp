<%@ page contentType="text/html; charset = UTF-8"%>
<html>
	<head>
		<link rel="icon" href="https://intland.com/wp-content/uploads/2019/09/favico.png" type="image/png" sizes="16x16"/>
		<title>Gabor Dicso test task for Intland</title>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		<script src="gabordicsotest.js"></script>
	</head>
	<body>
		<h2>${message}</h2>
<!-- 		<form method="POST" action="/node" target="_blank"> -->
			Parent id: <input type="text" name="parentId" value="1" /><br />
			Name: <input type="text" name="name" value="name" /><br />
			Content: <input type="text" name="content" value="content" /><br /> <input
				type="submit" onclick="test()" value="POST test" />
<!-- 		</form> -->
	</body>
</html>