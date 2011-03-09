package org.spockframework.springintegration;

import org.hsqldb.jdbc.JDBCDriver;

/**
 * @author janm
 */
@Jndi(
		dataSources = @DataSource(name = "java:comp/env/jdbc/test",
				driverClass = JDBCDriver.class, url = "jdbc:hsqldb:mem:test"),
		mailSessions = @MailSession(name = "java:comp/env/mail/foo", properties = "mail.smtp.host=localhost"),
		transactionManager = @TransactionManager(name = "java:comp/TransactionManager"),
		beans = @Bean(name = "java:comp/env/bean/some", type = SomeBean.class),
		jms = @Jms(
				connectionFactoryName = "java:comp/env/jms/connectionFactory",
				queues = {@Queue(name = "java:comp/env/jms/qa"), @Queue(name = "java:comp/env/jms/qb")},
				topics = {@Topic(name = "java:comp/env/jms/ta"), @Topic(name = "java:comp/env/jms/tb")}
		),
		builder = CustomJndiBuilder.class
)
public class JndiAnnotated {
}
