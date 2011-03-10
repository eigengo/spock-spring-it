package org.spockframework.springintegration.web;

import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

/**
 * @author janm
 */
public class SpringWebExtension implements IGlobalExtension {
	@Override
	public void visitSpec(SpecInfo spec) {
		final WebContextConfiguration webContextConfiguration = AnnotationUtils.findAnnotation(spec.getReflection(), WebContextConfiguration.class);
		if (webContextConfiguration == null) return;

		final File root = new File(".");
		File webXml = findWebSource(root, "src", "main", "webapp", "WEB-INF", "web.xml");
		File webinf = webXml.getParentFile();
		File webapp = webinf.getParentFile();

		try {
			MockServletContext servletContext = new MockServletContext(webapp.getAbsolutePath(), new AbsoluteFilesystemResourceLoader());
			MockServletConfig servletConfig = new MockServletConfig(servletContext);
			servletContext.addInitParameter("contextConfigLocation",
					StringUtils.arrayToDelimitedString(webContextConfiguration.contextConfiguration().value(), "\n"));
			String servletContextConfiguration = webContextConfiguration.servletContextConfiguration();
			if (servletContextConfiguration.isEmpty()) {
				throw new WebTestContextCreationException("You must specify servletContextConfiguration at this moment.");
			}
			servletConfig.addInitParameter("contextConfigLocation", servletContextConfiguration);

			ContextLoaderListener listener = new ContextLoaderListener();
			listener.initWebApplicationContext(servletContext);

			final DispatcherServlet dispatcherServlet = new TracingDispatcherServlet();
			dispatcherServlet.init(servletConfig);
			DispatcherServletHolder.set(dispatcherServlet);
		} catch (Exception e) {
			throw new WebTestContextCreationException(e);
		}

		/*
		SpringTestContextManager manager = new SpringTestContextManager(spec.getReflection());
		final SpringInterceptor interceptor = new SpringInterceptor(manager);

		spec.addListener(new AbstractRunListener() {
		  public void error(ErrorInfo error) {
			interceptor.error(error);
		  }
		});

		SpecInfo topSpec = spec.getTopSpec();
		topSpec.getSetupSpecMethod().addInterceptor(interceptor);
		topSpec.getSetupMethod().addInterceptor(interceptor);
		topSpec.getCleanupMethod().addInterceptor(interceptor);
		topSpec.getCleanupSpecMethod().addInterceptor(interceptor);
		*/
	}

	private File findWebSource(File root, String... path) {
		File webXml = findFile(root, path);
		if (webXml != null) return webXml;
		for (File directory : root.listFiles()) {
			if (!directory.isDirectory()) continue;
			webXml = findWebSource(directory, path);
			if (webXml != null) return webXml;
		}
		return null;
	}

	private File findFile(File root, String... path) {
		File subpath = root;
		for (int i = 0; i < path.length; i++) {
			subpath = new File(subpath, path[i]);
			if (!subpath.exists()) return null;
		}
		return subpath;
	}

}
