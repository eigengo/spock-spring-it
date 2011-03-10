package org.spockframework.springintegration.web.examples.web

import org.hsqldb.jdbc.JDBCDriver
import org.spockframework.springintegration.Bean
import org.spockframework.springintegration.DataSource
import org.spockframework.springintegration.Jndi
import org.spockframework.springintegration.web.WebContextConfiguration
import org.spockframework.springintegration.web.WebSpecification
import org.spockframework.springintegration.web.examples.domain.HibernateProperties
import org.springframework.test.context.ContextConfiguration

/**
 * @author janm
 */
@WebContextConfiguration(
	servletContextConfiguration = "/WEB-INF/sw-servlet.xml",
	contextConfiguration = @ContextConfiguration("classpath*:/META-INF/spring/module-context.xml")
)
@Jndi(
		dataSources = @DataSource(name = "java:comp/env/jdbc/test",
				driverClass = JDBCDriver.class, url = "jdbc:hsqldb:mem:test"),
		beans = @Bean(name = "java:comp/env/bean/hibernateProperties", type = HibernateProperties.class)
)
class IndexControllerTest extends WebSpecification {

	def home() {
		when:
		def param = "hello"
		def wo = get("/home/%s", param)

		then:
		wo.modelAttribute("message") == param
	}

}
