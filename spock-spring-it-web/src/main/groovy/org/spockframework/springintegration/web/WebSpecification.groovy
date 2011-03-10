package org.spockframework.springintegration.web

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import javax.servlet.RequestDispatcher

/**
 * @author janm
 */
class WebSpecification extends Specification {

	public WebObject get(String url, Object... params) {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", String.format(url, params)) {
			@Override
			RequestDispatcher getRequestDispatcher(String path) {
				return super.getRequestDispatcher(path);
			}

		};
		MockHttpServletResponse response = new MockHttpServletResponse();
		try {
			DispatcherServletHolder.get().service(request, response);
		} catch (Exception e) {

		}

		return new WebObject(request, response);
	}

}