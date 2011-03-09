package org.spockframework.springintegration;

import org.junit.Test;
import org.spockframework.runtime.model.SpecInfo;

import javax.jms.*;
import javax.jms.Queue;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.*;
import javax.transaction.TransactionManager;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author janm
 */
public class JndiExtensionTest {

	public void setupWith(Class<?> annotatedClass) {
		SpecInfo specInfo = new SpecInfo();
		specInfo.setReflection(annotatedClass);
		new JndiExtension().visitSpec(specInfo);
	}

	@Test
	public void dataSource() throws NamingException {
		setupWith(JndiAnnotated.class);
		final DataSource dataSource = lookup("java:comp/env/jdbc/test", DataSource.class);
		assertThat(dataSource, notNullValue());
	}

	@Test
	public void mailSession() throws NamingException {
		setupWith(JndiAnnotated.class);
		final Session session = lookup("java:comp/env/mail/foo", Session.class);
		assertThat(session.getProperty("mail.smtp.host"), is("localhost"));
	}
	
	@Test
	public void bean() throws NamingException {
		setupWith(JndiAnnotated.class);
		SomeBean bean = lookup("java:comp/env/bean/some", SomeBean.class);
		assertThat(bean, notNullValue());
	}
	
	@Test
	public void transactionManager() throws NamingException, SystemException, NotSupportedException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
		setupWith(JndiAnnotated.class);
		final TransactionManager transactionManager = lookup("java:comp/TransactionManager", TransactionManager.class);
		assertThat(transactionManager, notNullValue());

		transactionManager.begin();

		transactionManager.commit();
	}

	@Test
	public void custom() throws NamingException {
		setupWith(JndiAnnotated.class);
		String s = lookup("java:comp/env/strings/jan", String.class);
		assertThat(s, is("Jan"));
	}

	@Test
	public void jms() throws NamingException {
		setupWith(JndiAnnotated.class);
		ConnectionFactory connectionFactory = lookup("java:comp/env/jms/connectionFactory", ConnectionFactory.class);
		assertThat(connectionFactory, notNullValue());
		assertThat(lookup("java:comp/env/jms/qa", javax.jms.Queue.class), notNullValue());
		assertThat(lookup("java:comp/env/jms/qb", javax.jms.Queue.class), notNullValue());
		assertThat(lookup("java:comp/env/jms/ta", javax.jms.Topic.class), notNullValue());
		assertThat(lookup("java:comp/env/jms/tb", javax.jms.Topic.class), notNullValue());
	}
	
	@Test
	public void annotatedAnnotation() throws NamingException {
		setupWith(IntegrationTestAnnotated.class);
		String s = lookup("java:comp/env/strings/jan", String.class);
		assertThat(s, is("Jan"));
	}

	private <T> T lookup(String name, Class<T> expectedType) throws NamingException {
		final Object o = new InitialContext().lookup(name);
		if (expectedType.isAssignableFrom(o.getClass())) return (T)o;

		throw new RuntimeException("Object at " + name + " is " + o.getClass().getName() + ", expected " + expectedType.getName());
	}
}
