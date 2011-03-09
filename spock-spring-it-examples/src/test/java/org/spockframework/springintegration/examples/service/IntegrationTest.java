package org.spockframework.springintegration.examples.service;

import org.hsqldb.jdbc.JDBCDriver;
import org.spockframework.springintegration.*;
import org.spockframework.springintegration.examples.domain.HibernateProperties;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
 
/**
 * @author janm
 */
@Jndi(
		dataSources = @DataSource(name = "java:comp/env/jdbc/test",
				driverClass = JDBCDriver.class, url = "jdbc:hsqldb:mem:test"),
		mailSessions = @MailSession(name = "java:comp/env/mail/foo"),
		transactionManager = @TransactionManager(name = "java:comp/TransactionManager"),
		beans = @Bean(name = "java:comp/env/bean/hibernateProperties", type = HibernateProperties.class)
)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {
}
