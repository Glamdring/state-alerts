<%@ page pageEncoding="UTF-8" %>
<%@ include file="includes.jsp" %>
<c:set var="currentPage" value="home" />
<c:set var="head">
<title>Statealerts</title>
</c:set>
<%@ include file="header.jsp" %>

<div style="position: absolute; right: 0px; text-align: center;">
    <div class="fb-like-box" data-href="http://www.facebook.com/computoser.music" data-width="292" data-show-faces="false" data-stream="false" data-header="false" style="text-align: left;"></div><br />
    <div style="margin-top: 12px;"><a href="https://twitter.com/computosermusic" class="twitter-follow-button" data-show-count="false">Follow @computosermusic</a></div>
    <!-- Enable when google make this stylable div class="g-plus" data-width="193" data-height="69" data-href="//plus.google.com/108816193839204785767" data-rel="publisher"></div-->
    <a href="http://play.google.com/store/apps/details?id=com.computoser">
        <img alt="Android app on Google Play" src="${staticRoot}/img/android-play.png" style="border: none; margin-top: 10px; margin-bottom: 10px;"/><br />
    </a>
    <c:if test="${bitcoinEnabled}">
        <img alt="Bitcoin accepted here" title="Bitcoin accepted here" src="${staticRoot}/img/bitcoin-accepted.png" style="width: 129px;"/>
    </c:if>
</div>
<div class="main">

<h1>Listen to unique, computer-generated music...</h1>
<p class="lead">Computoser is an algorithm that turns the computer into a music composer.<br />
Each track you hear is algorithmically generated.</p>
<%@ include file="signin.jsp" %>

<hr />
<a href="${root}/rss" target="_blank">
    <img src="${staticRoot}/img/rss.png" style="border-style: none; width: 36px; height: 36px; margin-right: 5px;" />
    Subscribe via RSS and receive some of the recently generated tracks
</a><br />
<c:if test="${!userLoggedIn}">
    <div style="margin-top: -5px;">For email subscription - please <a href="${root}/signup">sign up</a>.</div>
</c:if>
</div>
<%@ include file="footer.jsp" %>