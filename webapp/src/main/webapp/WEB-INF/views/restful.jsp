<%@ include file="includes.jsp" %>

<!DOCTYPE html>
<html>
<head>
  <title>State Alerts RESTful services</title>
  <link href="//fonts.googleapis.com/css?family=Droid+Sans:400,700" rel="stylesheet" type="text/css"/>
  <link href="${staticRoot}/swagger/css/highlight.default.css" media="screen" rel="stylesheet" type="text/css"/>
  <link href="${staticRoot}/swagger/css/screen.css" media="screen" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="${staticRoot}/swagger/lib/shred.bundle.js" /></script>  
  <script src="${staticRoot}/swagger/lib/jquery-1.8.0.min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/jquery.slideto.min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/jquery.wiggle.min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/jquery.ba-bbq.min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/handlebars-1.0.0.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/underscore-min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/backbone-min.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/swagger.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/swagger-ui.js" type="text/javascript"></script>
  <script src="${staticRoot}/swagger/lib/highlight.7.3.pack.js" type="text/javascript"></script>

    <script type="text/javascript">
    //hacky
    window.authorizations.add("json", new ApiKeyAuthorization("Accept", "application/json", "header"));
    $(function () {
      window.swaggerUi = new SwaggerUi({
      url: "<c:url value="/api-docs" />",
      dom_id: "swagger-ui-container",
      supportedSubmitMethods: ['get', 'post', 'put', 'delete'],
      onComplete: function(swaggerApi, swaggerUi){
        if(console) {
          console.log("Loaded SwaggerUI")
        }
        $('pre code').each(function(i, e) {hljs.highlightBlock(e)});
      },
      onFailure: function(data) {
        if(console) {
          console.log("Unable to Load SwaggerUI");
          console.log(data);
        }
      },
      docExpansion: "none"
    });

    window.swaggerUi.load();
  });

  </script>
</head>

<body>
<div align="center"><h1>State Alerts RESTful API reference</h1></div>
<div id="message-bar" class="swagger-ui-wrap">
    &nbsp;
</div>

<div id="swagger-ui-container" class="swagger-ui-wrap">

</div>

</body>

</html>