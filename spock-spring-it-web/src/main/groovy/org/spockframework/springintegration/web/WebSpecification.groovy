package org.spockframework.springintegration.web

import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import org.spockframework.springintegration.web.webobject.HtmlForm
import org.spockframework.springintegration.web.webobject.WebObject
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.servlet.ModelAndView
import spock.lang.Specification

/**
 * @author janm
 */
@ContextConfiguration(loader = ExistingApplicationContextLoader)
class WebSpecification extends Specification {

	public WebObject get(String url, Object... params) {
		JspCapableMockHttpServletRequest request = new JspCapableMockHttpServletRequest("GET", String.format(url, params),
			DispatcherServletHolder.get().servletConfig)
		return doService(request)
	}

	public WebObject post(WebObject wo) {
		HtmlForm form = wo.singleForm
		def action = form.action
		def method = form.method
		JspCapableMockHttpServletRequest request = new JspCapableMockHttpServletRequest(method, action,
			DispatcherServletHolder.get().servletConfig)
		form.inputs.each { i -> request.setParameter(i.id, i.values) }
		return doService(request)
	}

	public WebObject post(String url, params) {
		JspCapableMockHttpServletRequest request = new JspCapableMockHttpServletRequest("POST", url,
			DispatcherServletHolder.get().servletConfig)
		params.each { k, v -> request.setParameter(k, v) }
		return doService(request)
	}

	private def doService(JspCapableMockHttpServletRequest request) {
		try {
			MockHttpServletResponse response = new MockHttpServletResponse()
			def dispatcherServlet = DispatcherServletHolder.get()
			def requestThread = new Thread(new Runnable() {
				void run() {
					dispatcherServlet.service((ServletRequest) request, (ServletResponse) response)
				}

			});
			requestThread.start()
			requestThread.join()

			final ModelAndView modelAndView = (ModelAndView) request.getAttribute(TracingDispatcherServlet.MODEL_AND_VIEW_KEY);
			return new WebObject(request, response, response.getContentAsByteArray(), modelAndView)
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}