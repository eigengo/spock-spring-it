package org.spockframework.springintegration.web.examples.web;

import org.hsqldb.jdbc.JDBCDriver;
import org.spockframework.springintegration.Bean;
import org.spockframework.springintegration.DataSource;
import org.spockframework.springintegration.Jndi;
import org.spockframework.springintegration.web.WebContextConfiguration;
import org.spockframework.springintegration.web.examples.domain.HibernateProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author janm
 */
@WebContextConfiguration(
	servletContextConfiguration = "/WEB-INF/sw-servlet.xml",
	contextConfiguration = @ContextConfiguration("classpath*:/META-INF/spring/module-context.xml")
)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@Jndi(
		dataSources = @DataSource(name = "java:comp/env/jdbc/test",
				driverClass = JDBCDriver.class, url = "jdbc:hsqldb:mem:test"),
		beans = @Bean(name = "java:comp/env/bean/hibernateProperties", type = HibernateProperties.class)
)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WebTest {
}
