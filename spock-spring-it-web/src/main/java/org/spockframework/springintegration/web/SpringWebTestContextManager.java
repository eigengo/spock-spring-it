package org.spockframework.springintegration.web;

import org.spockframework.util.ReflectionUtil;
import org.springframework.test.context.TestContextManager;

import java.lang.reflect.Method;

/**
 * @author janm
 */
class SpringWebTestContextManager {
	private static final Method beforeTestClassMethod =
			ReflectionUtil.getMethodBySignature(TestContextManager.class, "beforeTestClass");
	private static final Method afterTestClassMethod =
			ReflectionUtil.getMethodBySignature(TestContextManager.class, "afterTestClass");

	private final TestContextManager delegate;

	public SpringWebTestContextManager(Class<?> testClass) {
		this.delegate = new TestContextManager(testClass, ExistingApplicationContextLoader.class.getCanonicalName());
	}

	public void beforeTestClass() throws Exception {
		if (beforeTestClassMethod != null)
			ReflectionUtil.invokeMethodThatThrowsException(this.delegate, beforeTestClassMethod);
	}

	public void afterTestClass() throws Exception {
		if (afterTestClassMethod != null)
			ReflectionUtil.invokeMethodThatThrowsException(this.delegate, afterTestClassMethod);
	}

	public void prepareTestInstance(Object testInstance) throws Exception {
		this.delegate.prepareTestInstance(testInstance);
	}

	public void beforeTestMethod(Object testInstance, Method testMethod) throws Exception {
		this.delegate.beforeTestMethod(testInstance, testMethod);
	}

	public void afterTestMethod(Object testInstance, Method testMethod, Throwable exception) throws Exception {
		this.delegate.afterTestMethod(testInstance, testMethod, exception);
	}
}
