package org.spockframework.springintegration.examples.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
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

	/*
	def "index"() {
		given:
		def message = this.hibernateTemplate.get(Message.class, 1L)

		when:
		form = get("/message/1.html")
		response = post(form)

		then:
		this.hibernateTemplate.get(Message.class, 1L).equals(message)
	}
	*/

}
