package org.spockframework.springintegration.web.examples.web

import org.spockframework.springintegration.web.WebSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.hibernate3.HibernateTemplate

/**
 * @author janm
 */
@WebTest
class UserControllerTest extends WebSpecification {
	@Autowired
	HibernateTemplate hibernateTemplate

	def "posting a user with all fields"() {
		given:
		def username = "__janm"
		def fullName = "Jan Machacek"

		when:
		post("/users.html", [username:username, fullName:fullName])

		then:
		def user = this.hibernateTemplate.find("from User where username=?", username)[0]
		user.fullName == fullName
	}

	def "posting with validation"() {
		given:
		def username = "janm"
		def fullName = "Jan Machace"

		when:
		def wp = post("/users.html", [username:"x", fullName:""])
		post("/users.html", [username:username, fullName:fullName])

		then:
		wp.hasFieldErrorFor("fullName")
		wp.hasFieldErrorFor("username")

		def user = this.hibernateTemplate.find("from User where username=?", username)[0]
		user.fullName == fullName
	}

}
