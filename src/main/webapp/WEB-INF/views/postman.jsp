<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%><html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>VMware Manager</title>


<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>



<script> 
$(document).ready(function(){
    $("#method").change(function(){
	if($( "#method option:selected" ).text()=='GET' || $("#method option:selected" ).text()=='DELETE')
	{
		$("#req").slideUp("slow");
	}
	else
	{
		$("#req").slideDown("slow");
	}
    });
    
    
    $("#url").focus(function(){
    	if(!$("#url"))
    	{
    		$("#send").prop("disabled",true);
    	}
    	else
    	{
    		$("#send").prop("disabled",false);
    	}
        });
       
    $("#url").blur(function(){
   	
    	if(!$("#url"))
    	{
    		$("#send").prop("disabled",true);
    	}
    	else
    	{
    		$("#send").prop("disabled",false);
    	}
    	
       });
      
    
});
</script>

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>


<link
	href="<c:url value="/resources/static/css/bootstrap.min.css"></c:url>"
	rel="stylesheet" />
<!-- Bootstrap Core CSS -->


<!-- Custom CSS -->
<link href="<c:url value="/resources/static/css/sb-admin.css"></c:url>"
	rel="stylesheet" />

<!-- Morris Charts CSS -->
<link
	href="<c:url value="/resources/static/css/plugins/morris.css"></c:url>"
	rel="stylesheet" />

<!-- Custom Fonts -->
<link
	href="<c:url value="/resources/static/font-awesome/css/font-awesome.min.css"></c:url>"
	rel="stylesheet" type="text/css" />

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>
<body>

	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/VMWareManager/postman">VMWare
					VMManager</a>
			</div>
			<!-- Top Menu Items -->
			<ul class="nav navbar-right top-nav">
			</ul>
			<!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav side-nav">
					<li><a href="/VMWareManager/vmdata"><i
							class="fa fa-fw fa-bar-chart-o"></i>All Virtual Machines</a></li>
					<li><a href="/VMWareManager/performance"><i
							class="fa fa-fw fa-bar-chart-o"></i>Healthy Virtual Machines</a></li>
					<li><a href="/VMWareManager/postman"><i
							class="fa fa-fw fa-desktop"></i>REST Client</a></li>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</nav>

		<div id="page-wrapper">

			<div class="container-fluid">

				<!-- Page Heading -->
				<div class="row">
					<div class="col-lg-12">
						<h1 class="page-header">Load Balancer</h1>
					</div>
				</div>
				<!-- /.row -->

				<div class="row">
					<form id = "postmanForm" name="postmanForm" action="/VMWareManager/send" method="post" novalidate="novalidate">
						<div class="col-lg-9">
							<div class="form-group">
								<label>Enter URL Here</label> <input id="url" name="url"
									class="form-control">
								<p class="help-block">Example http://www.google.com</p>
							</div>
						</div>
						<div class="col-lg-3">
							<div class="form-group">
								<label>Select Method</label> <select id="method" name="method"
									class="form-control">
									
									
									<option value="POST">POST</option>
									<option value="GET">GET</option>
									<option value="PUT">PUT</option>
									<option value="DELETE">DELETE</option>
								</select>
							</div>

						</div>
				</div>

				<div class="row">
					<div class="col-lg-12">



						<div class="form-group">
							<div id="req" class="form-group">
								<label id="request_body">Request Body</label>
								<textarea id="request" name="request" class="form-control"
									rows="4"></textarea>
							</div>
							<button id="send" name="send" type="submit" disabled="true"
								class="btn btn-primary">Send</button>
							<button id="reset" name="reset" type="reset"
								class="btn btn-primary">Reset</button>

						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-lg-12">
						<br>

						<div class="form-group">
							<label>Response Body</label><br>
							<textarea id="response" name="response" class="form-control" readonly
								rows="8">${response}</textarea>

						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-lg-3">
						<br>

						<div class="form-group">
							<label>Responding VM IP</label><br>
						${response_from}
						</div>
					</div>
					
					<div class="col-lg-3">
						<br>

						<div class="form-group">
							<label>Responding VM Name</label><br>
						${response_name}
						</div>
					</div>
				</div>
				</form>

				<!-- /.row -->
			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>
	<!-- jQuery -->
	<script src="<c:url value="/resources/static/js/jquery.js"></c:url>"></script>

	<!-- Bootstrap Core JavaScript -->
	<script
		src="<c:url value="/resources/static/js/bootstrap.min.js"></c:url>">
		
	</script>

	<!-- Morris Charts JavaScript -->
	<script
		src="<c:url value="/resources/static/js/plugins/morris/raphael.min.js"></c:url>">
		
	</script>
	<script
		src="<c:url value="/resources/static/js/plugins/morris/morris.min.js"></c:url>">
		
	</script>
	<script
		src="<c:url value="/resources/static/js/plugins/morris/morris-data.js"></c:url>"></script>
</body>


</html>
