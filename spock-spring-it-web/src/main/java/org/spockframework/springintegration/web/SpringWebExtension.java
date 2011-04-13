package org.spockframework.springintegration.web;

import org.spockframework.runtime.AbstractRunListener;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.ErrorInfo;
import org.spockframework.runtime.model.SpecInfo;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
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
		ContextConfiguration contextConfiguration = AnnotationUtils.findAnnotation(spec.getReflection(), ContextConfiguration.class);
		if (contextConfiguration.loader() != ExistingApplicationContextLoader.class ||
				contextConfiguration.value().length > 0 ||
				contextConfiguration.locations().length > 0) {
			throw new WebTestContextCreationException("Do not annotate your web test cases with @ContextConfiguration. Extend WebSpecification instead.");
		}

		final String[] webContextConfigurationResources = webContextConfiguration.value();
		String[] filesToFind = new String[webContextConfigurationResources.length + 1];
		for (int i = 0; i < webContextConfigurationResources.length; i++) {
			filesToFind[i + 1] = webContextConfigurationResources[i];
		}
		filesToFind[0] = "/WEB-INF/web.xml";
		final File root = new File(".");
		File webapp = findWebSource(root,
				new String[] {"src", "main", "webapp"},
				filesToFind);
		File webinf = new File(webapp, "WEB-INF");// webXml.getParentFile();
		//File webapp = webinf.getParentFile();

		try {
			MockServletContext servletContext = new MockServletContext(webapp.getAbsolutePath(), new AbsoluteFilesystemResourceLoader());
			MockServletConfig servletConfig = new MockServletConfig(servletContext);
			servletContext.addInitParameter("contextConfigLocation",
					StringUtils.arrayToDelimitedString(webContextConfiguration.contextConfiguration().value(), "\n"));
			String[] servletContextConfiguration = webContextConfigurationResources;
			if (servletContextConfiguration.length == 0) {
				throw new WebTestContextCreationException("You must specify servletContextConfiguration at this moment.");
			}
			servletConfig.addInitParameter("contextConfigLocation",
					StringUtils.arrayToDelimitedString(servletContextConfiguration, "\n"));

			ContextLoaderListener listener = new ContextLoaderListener();
			listener.initWebApplicationContext(servletContext);

			final DispatcherServlet dispatcherServlet = new TracingDispatcherServlet();
			dispatcherServlet.init(servletConfig);
			DispatcherServletHolder.set(dispatcherServlet);
		} catch (Exception e) {
			throw new WebTestContextCreationException(e);
		}

		SpringWebTestContextManager manager = new SpringWebTestContextManager(spec.getReflection());
		final SpringWebInterceptor interceptor = new SpringWebInterceptor(manager);

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
	}

	private File findWebSource(File root, String[] path, String[] filesToFind) {
		File webInf = findFile(root, path);
		if (webInf != null) return webInf;
		for (File directory : root.listFiles()) {
			if (!directory.isDirectory()) continue;
			webInf = findWebSource(directory, path, filesToFind);
			if (webInf != null) {
				boolean foundAllFiles = true;
				for (int i = 0; i < filesToFind.length; i++) {
					if (!(new File(webInf, filesToFind[i])).exists()) {
						foundAllFiles = false;
						break;
					}
				}

				if (foundAllFiles) return webInf;
			}
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
