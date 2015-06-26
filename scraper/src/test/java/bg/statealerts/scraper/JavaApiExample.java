package bg.statealerts.scraper;

import bg.statealerts.scraper.config.ContentLocationTypeConstants;
import bg.statealerts.scraper.config.DocumentTypeConstants;
import bg.statealerts.scraper.config.ExtractorDescriptorBuilder;
import bg.statealerts.scraper.config.HttpRequestBuilder;

public class JavaApiExample {

	public static void main(String[] args) {
		ExtractorDescriptorBuilder builder = new ExtractorDescriptorBuilder();
		builder.setContentLocationType(ContentLocationTypeConstants.TABLE());
		builder.setDocumentType(DocumentTypeConstants.HTML());
		
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		requestBuilder.setBodyParams("a=b");
		requestBuilder.setMethod("POST");
		builder.setHttpRequest(requestBuilder.build());
		
		builder.build();
	}
}
