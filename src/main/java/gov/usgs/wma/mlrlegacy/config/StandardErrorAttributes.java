package gov.usgs.wma.mlrlegacy.config;

import java.util.Map;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import gov.usgs.wma.mlrlegacy.GlobalDefaultExceptionHandler;

@Component
public class StandardErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest request, boolean includeStackTrace) {
		Map<String, Object> errorAttributes = super.getErrorAttributes(request, includeStackTrace);

		if(errorAttributes.containsKey("message")) {
			errorAttributes.put(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY, errorAttributes.get("message"));
		} else if(errorAttributes.containsKey("error")) {
			errorAttributes.put(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY, errorAttributes.get("error"));
		}

		return errorAttributes;
	}
}