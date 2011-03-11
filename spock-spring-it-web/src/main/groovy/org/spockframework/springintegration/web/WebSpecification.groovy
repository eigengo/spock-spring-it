package org.spockframework.springintegration.web

import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

 /**
 * @author janm
 */
@ContextConfiguration(loader = ExistingApplicationContextLoader)
class WebSpecification extends Specification {

	public WebObject get(String url, Object... params) {
		JspCapableMockHttpServletRequest request = new JspCapableMockHttpServletRequest("GET", String.format(url, params))
		MockHttpServletResponse response = new MockHttpServletResponse()
		try {
			DispatcherServletHolder.get().service(request, response)
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new WebObject(request, response)
	}

	public WebObject post(String url, params) {
		JspCapableMockHttpServletRequest request = new JspCapableMockHttpServletRequest("POST", String.format(url, params))
		params.each { k, v -> request.setParameter(k, v) }
		MockHttpServletResponse response = new MockHttpServletResponse()
		try {
			DispatcherServletHolder.get().service(request, response)
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new WebObject(request, response)
	}

}