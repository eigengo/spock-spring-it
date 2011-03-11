package org.spockframework.springintegration.web;

import org.apache.jasper.servlet.JspServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockRequestDispatcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author janm
 */
public class JspCapableMockHttpServletRequest extends MockHttpServletRequest {

	public JspCapableMockHttpServletRequest() {
	}

	public JspCapableMockHttpServletRequest(String method, String requestURI) {
		super(method, requestURI);
	}

	public JspCapableMockHttpServletRequest(ServletContext servletContext) {
		super(servletContext);
	}

	public JspCapableMockHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
		super(servletContext, method, requestURI);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new RD(path);
	}

	static class RD extends MockRequestDispatcher {
		private String url;
		/**
		 * Create a new MockRequestDispatcher for the given URL.
		 *
		 * @param url the URL to dispatch to.
		 */
		public RD(String url) {
			super(url);
			this.url = url;
		}

		@Override
		public void forward(ServletRequest request, ServletResponse response) {
			execute((HttpServletRequest) request, response);
		}

		@Override
		public void include(ServletRequest request, ServletResponse response) {
			execute((HttpServletRequest) request, response);
		}

		private void execute(final HttpServletRequest request, ServletResponse response) {
			JspServlet s = new JspServlet();
			try {
				s.init(DispatcherServletHolder.get().getServletConfig());
				s.service(new HttpServletRequestWrapper((HttpServletRequest)request) {
					@Override
					public String getServletPath() {
						return url;
					}
				}, response);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
