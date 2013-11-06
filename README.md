state-alerts
============

Get email alerts whenever a certain keyword is used in publications from set of state institutions - government, parliament, agencies, etc.

The project has two subprojects:

* the scraper - a separate configurable module that performs scraping documents from various sites
* the webapp - a webapp that is using the scraper, stores and indexes documents

scraper
=======

This is the module that is reusable. If you need to scrape something, checkout the project, build it and get `scraper.jar`.

The class used to configure individual scraping instances is `ExtractorDescriptor`. There you specify a number of things:

* Target URL, http method, body parameters (in case of POST). You can put a placeholder `{x}` which will be used for paging
* The type of document (PDF, doc, HTML) and the type of the scraping workflow – i.e. how is the document reached on the target page. There are 4 options, depending on whether there’s a separate details page, whether there’s only a table and where the link to the document is located
* XPath expressions for elements, containing meta data and the links to the documents. There’s a different expression depending on where the information is located – in a table or in separate details page
* Date format, for the date of the document; optionally regex can be used, in case the date cannot be strictly located by XPath
* Simple “heuristics” – if you know the URL structure of the document you are looking for, there’s no need to locate it via XPath.
* Other configurations, like javascript requirements, whether scraping should fail on error, etc.

When you have an `ExtractorDescriptor` instance ready (for java apps you can use the builder to create one), you can create a `new Extractor(descriptor)`, and then (usually with a scheduled job) call `extractor.extractDocuments(since)`

The result is a list of documents.

More information <a href="http://techblog.bozho.net/?p=1215">in this article</a> 

webapp
=======

The webapp is a ready-to-use i18nizable web-application that can be deployed with just a few steps (by default we deploy it at OpenShift). It stores and indexes the scraped documents and provides a UI for searching and subscribing for email alerts. 