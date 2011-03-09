package org.spockframework.springintegration;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the {@link javax.activation.DataSource} to be added to the JNDI environment
 *
 * @author janm
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSource {

	/**
	 * The name in the JNDI environment; typically something like <code>java:comp/env/bean/xyz</code>
	 *
	 * @return the JNDI name
	 */
	String name();

	/**
	 * Specifies the class name of the driver
	 *
	 * @return the class name of the driver
	 */
	String driverClassName();

	/**
	 * The JDBC URL
	 *
	 * @return the URL
	 */
	String url();

	/**
	 * The username to establish the DB connection
	 *
	 * @return the username
	 */
	String username() default "sa";

	/**
	 * The password to establish the DB connection
	 *
	 * @return the password
	 */
	String password() default "";

}
