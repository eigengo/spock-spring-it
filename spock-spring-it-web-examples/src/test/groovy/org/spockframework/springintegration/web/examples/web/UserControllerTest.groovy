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
		// send invalid form
		def wp = post("/users.html", [username:"x", fullName:""])
		// correct the errors, submit again
		post("/users.html", [username:username, fullName:fullName])

		then:
		// the result of the first post should have validation errors
		wp.hasFieldErrorFor("fullName")
		wp.hasFieldErrorFor("username")

		// because of the second valid post, we should now have the user in the DB
		def user = this.hibernateTemplate.find("from User where username=?", username)[0]
		user.fullName == fullName
	}

}
