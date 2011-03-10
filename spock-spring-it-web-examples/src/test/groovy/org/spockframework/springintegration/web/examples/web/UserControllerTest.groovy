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
		//def wp = get("/users/1.html")
		//post(wp)

		then:
		def user = this.hibernateTemplate.find("from User where username=?", username)[0]
		user.fullName == fullName
	}

	def "posting a user missing fullName"() {
		given:

		when:
		def wp = post("/users.html", [username:"x", fullName:""])

		then:
		//wp.hasErrors
		//wp.hasErrorFor("fullName")
		true
	}

}
