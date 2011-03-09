package org.spockframework.springintegration.examples.service

import org.springframework.test.context.ContextConfiguration
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

/**
 * @author janm
 */
@IntegrationTest
@ContextConfiguration(locations = "classpath*:/META-INF/spring/module-context.xml")
class SomeServiceTest extends Specification {
	@Autowired
	SomeService someService

	def "expect empty list"() {
		expect:
		this.someService.idolFind().empty
	}

}
