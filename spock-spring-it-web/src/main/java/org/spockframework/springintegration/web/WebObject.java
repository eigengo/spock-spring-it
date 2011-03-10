package org.spockframework.springintegration.web;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author janm
 */
public class WebObject {
	private byte[] content;
	private Map<String, Object> model;

	WebObject(HttpServletRequest request, MockHttpServletResponse response) {
		final ModelAndView modelAndView = (ModelAndView) request.getAttribute(TracingDispatcherServlet.MODEL_AND_VIEW_KEY);
		this.model = modelAndView.getModel();
		this.content = response.getContentAsByteArray();
	}

	private BindingResult getRequiredBindingResultFor(String name) {
		return BindingResultUtils.getRequiredBindingResult(this.model, name);
	}

	private BindingResult getRequiredSingleBindingResult() {
		BindingResult result = null;
		for (Map.Entry<String, Object> e : this.model.entrySet()) {
			if (e.getValue() instanceof BindingResult) {
				if (result != null) throw new RuntimeException("More than one BindingResult found.");
				result = (BindingResult) e.getValue();
			}
		}
		if (result == null) throw new RuntimeException("No BindingResult found.");
		return result;
	}

	public String html() {
		return new String(this.content);
	}

	public Object modelAttribute(String name) {
		return modelAttribute(name, Object.class);
	}

	public <T> T modelAttribute(String name, Class<T> type) {
		final Object o = this.model.get(name);
		if (o == null) return null;
		if (o.getClass().isAssignableFrom(type)) throw new RuntimeException("");
		return (T)o;
	}

	public boolean hasFieldErrorFor(String path) {
		return getRequiredSingleBindingResult().hasFieldErrors(path);
	}
}
