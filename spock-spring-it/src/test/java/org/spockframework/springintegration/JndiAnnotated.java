package org.spockframework.springintegration;

/**
 * @author janm
 */
@Jndi(
		dataSources = @DataSource(name = "java:comp/env/jdbc/test",
				driverClassName = "org.hsqldb.jdbcDriver", url = "jdbc:hsqldb:mem:test"),
		mailSessions = @MailSession(name = "java:comp/env/mail/foo", properties = "mail.smtp.host=localhost"),
		transactionManager = @TransactionManager(name = "java:comp/TransactionManager"),
		beans = @Bean(name = "java:comp/env/bean/some", type = SomeBean.class),
		builder = CustomJndiBuilder.class
)
public class JndiAnnotated {
}
