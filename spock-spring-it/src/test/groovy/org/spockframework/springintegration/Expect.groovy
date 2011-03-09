package org.spockframework.springintegration

import javax.persistence.EntityManager
import spock.lang.Specification

 /**
 * @author janm
 */
class Expect extends Specification {

	def x() {
		given:
		def em = Mock(EntityManager)
		def s = new SomeService(entityManager: em)

		when:
		s.idolFind("test")

		then:
		1 * em.persist({it.title == "test"; it.id == 5L})
	}

}
