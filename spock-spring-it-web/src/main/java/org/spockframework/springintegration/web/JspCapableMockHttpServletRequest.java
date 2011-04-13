package org.spockframework.springintegration.web;

import org.apache.jasper.servlet.JspServlet;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockRequestDispatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author janm
 */
public class JspCapableMockHttpServletRequest extends MockHttpServletRequest {

	private final ServletConfig servletConfig;

	public JspCapableMockHttpServletRequest(String method, String requestURI,
											ServletConfig servletConfig) {
		super(method, requestURI);
		this.servletConfig = servletConfig;
		setRequestedSessionId("x");
		setRequestedSessionIdValid(true);
		setRequestedSessionIdFromCookie(true);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return new RD(path, this.servletConfig);
	}

	static class RD extends MockRequestDispatcher {
		private final String url;
		private final ServletConfig servletConfig;
		/**
		 * Create a new MockRequestDispatcher for the given URL.
		 *
		 * @param url the URL to dispatch to.
		 */
		public RD(String url, ServletConfig servletConfig) {
			super(url);
			this.url = url;
			this.servletConfig = servletConfig;
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
				s.init(this.servletConfig);
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
