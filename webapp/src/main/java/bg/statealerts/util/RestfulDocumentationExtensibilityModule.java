package bg.statealerts.util;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

import org.springframework.util.ReflectionUtils;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.configuration.ExtensibilityModule;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;

public class RestfulDocumentationExtensibilityModule extends ExtensibilityModule {
	
	@Inject
	private SwaggerConfiguration config;
	
	private String apiPathPrefix = "/api";
	public void setApiPathPrefix(String apiPathPrefix) {
		this.apiPathPrefix = apiPathPrefix;
	}
	
	protected void customizeDocumentationFilters(
			List<Filter<Documentation>> documentationFilters) {
		documentationFilters.add(new Filter<Documentation>() {
			@Override
			public void apply(FilterContext<Documentation> context) {
//				Documentation replacement = new FilteringDocumentation(null, config.getApiVersion(), config.getBasePath(), null, apiPathPrefix);
//				Field fld = ReflectionUtils.findField(FilterContext.class, "subject");
//				ReflectionUtils.makeAccessible(fld);
//				ReflectionUtils.setField(fld, context, replacement);
			}
		});
	}
	
//	public static class FilteringDocumentation extends Documentation  {
//		
//		private String apiPathPrefix;
//		
//		public FilteringDocumentation(String apiVersion, String swaggerVersion,
//				String basePath, String resourcePath, String apiPathPrefix) {
//			super(apiVersion, swaggerVersion, basePath, resourcePath);
//			this.apiPathPrefix = apiPathPrefix;
//		}
//		
//		public FilteringDocumentation() {
//		}
//		
//		@Override
//		public Object addApi(DocumentationEndPoint ep) {
//			if (ep.getPath().startsWith("/" + DocumentationController.CONTROLLER_ENDPOINT + apiPathPrefix)) {
//				return super.addApi(ep);
//			}
//			return ep;
//		}
//	}
}
