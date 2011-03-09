package org.spockframework.springintegration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

 /**
 * @author janm
 */
@IntegrationTest
@ContextConfiguration(locations = "classpath*:/META-INF/spring/module-context.xml")
class Expect2 extends Specification {
	@Autowired
	FooService service

	def y() {
		expect:
		result == this.service.x(param)

		where:
		param	| 	result
		"one"	|	3
		"two"	|	3
		"four"	|	4
	}

}
