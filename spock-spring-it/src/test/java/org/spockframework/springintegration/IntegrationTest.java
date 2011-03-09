package org.spockframework.springintegration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author janm
 */
@Jndi(
		builder = CustomJndiBuilder.class
)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
