<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
<link rel="shortcut icon" href="${staticRoot}/img/favicon.png" />
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.min.css" rel="stylesheet" type="text/css" media="screen" />
<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/js/bootstrap.min.js" rel="stylesheet" type="text/css" media="screen"/>
<link href="${staticRoot}/styles/main.css" rel="stylesheet" type="text/css" media="screen"/>

<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script type="text/javascript" src="https://login.persona.org/include.js"></script>
<script type="text/javascript" src="${staticRoot}/scripts/bootstrap.min.js"></script>
${head}
<meta name="description" content="" />
<meta name="keywords" content="" />
<script type="text/javascript">
    var loggedInUser = ${context.user != null ? '"' + context.user.email + '"' : 'null'};
    var userRequestedAuthentication = false;
    $(document).ready(function() {
        navigator.id.watch({
            loggedInUser : loggedInUser,
            onlogin : function(assertion) {
                $.ajax({
                    type : 'POST',
                    url : '${root}/persona/auth',
                    data : {assertion : assertion, userRequestedAuthentication : userRequestedAuthentication},
                    success : function(data) {
                        if (data != '') {
                            window.location.href = '${root}' + data;
                        }
                    },
                    error : function(xhr, status, err) {
                        alert("Authentication failure: " + err);
                    }
                });
            },
            onlogout : function() {
                window.locaiton.open("${root}/logout");
            }
        });
    });
</script>
</head>
<body>
<c:if test="${currentPage =='home'}">
    <div id="fb-root"></div>
    <script>(function(d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) return;
      js = d.createElement(s); js.id = id;
      js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk'));</script>
    <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
</c:if>
    <div class="container">
        <div class="masthead">
            <ul class="nav nav-pills pull-right">
                <li <c:if test="${currentPage == 'home'}">class="active"</c:if>><a href="${root}/">${msg.home}</a></li>
                <c:if test="${userLoggedIn}">
                    <li <c:if test="${currentPage == 'myalerts'}">class="active"</c:if>><a href="${root}/alerts/list">${msg.myAlerts}</a></li>
                </c:if>
                <li <c:if test="${currentPage == 'topalerts'}">class="active"</c:if>><a href="${root}/toprecent">${msg.topAlerts}</a></li>
                <c:if test="${!userLoggedIn}">
                    <li <c:if test="${currentPage == 'signup'}">class="active"</c:if>><a href="${root}/signup">${msg.signup}</a></li>
                </c:if>
                <li <c:if test="${currentPage == 'about'}">class="active"</c:if>><a href="${root}/about">${msg.about}</a></li>
                <c:if test="${userLoggedIn}">
                    <li><a href="${root}/logout" onclick="navigator.id.logout();">${msg.logout}</a></li>
                </c:if>
            </ul>
            <h3><a href="${root}/" class="muted"><img src="${staticRoot}/img/logo.png" class="logo" />Известия</a></h3>
        </div>
        <hr style="width: 100%;"/>
        <c:if test="${!empty param.message}">
            <div style="color: green; text-align: center; font-size: 15pt;">${param.message}</div>
        </c:if>