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

	def "create, view and edit"() {
		given:
		def username = "__janm"

		when:
		post("/users.html", [username:username, fullName:"Jan Machacek"])
		def wo = get("/users/1.html")
		wo.setValue("#fullName", "Jan")
		post(wo)

		then:
		def user = this.hibernateTemplate.find("from User where username=?", username)[0]
		user.fullName == "Jan"
	}

	def "posting with validation"() {
		given:
		def username = "janm"
		def fullName = "Jan Machacek"

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
