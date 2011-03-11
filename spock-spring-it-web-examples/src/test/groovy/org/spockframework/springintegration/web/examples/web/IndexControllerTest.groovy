package org.spockframework.springintegration.web.examples.web

import org.spockframework.springintegration.web.WebSpecification
import org.spockframework.springintegration.web.examples.domain.Message
import org.spockframework.springintegration.web.examples.services.ManagementService
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author janm
 */
@WebTest
class IndexControllerTest extends WebSpecification {
	@Autowired
	def ManagementService managementService;

	def home() {
		when:
		def param = "hello"
		def wo = get("/home/%s", param)

		then:
		wo.modelAttribute("message") == param
		wo.html().contains param
		System.out.println(wo.html());
		this.managementService.get(Message, 1L).sourceText == param
	}

}
