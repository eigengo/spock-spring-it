package org.spockframework.springintegration;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the {@link javax.transaction.TransactionManager} to be added to the JNDI environment
 *
 * @author janm
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TransactionManager {
	/**
	 * The name in the JNDI environment; typically something like <code>java:comp/env/TransactionManager</code>
	 *
	 * @return the JNDI name
	 */
	String name();

	/**
	 * Specified that the transaction manager should be a two-phase commit manager. Typically, you would
	 * leave this value as {@code false}.
	 *
	 * @return {@code true} if the TransactionManager should be an XA one
	 */
	boolean xa() default false;

}
