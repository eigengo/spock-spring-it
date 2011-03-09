package org.spockframework.springintegration.examples.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.integration.MessageChannel
import org.springframework.integration.support.MessageBuilder
import org.springframework.orm.hibernate3.HibernateTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * @author janm
 */
@IntegrationTest
@ContextConfiguration(locations = "classpath*:/META-INF/spring/module-context.xml")
class MessageDrivenTest extends Specification implements ApplicationContextAware {
	MessageChannel queue;
	@Autowired
	HibernateTemplate hibernateTemplate

	def "message driven code"() {
		expect:
		this.queue.send(MessageBuilder.withPayload(message).build())
		savedMessage == this.hibernateTemplate.find("from Message where sourceText=?", message)[0].processedText

		where:
		message 		|	savedMessage
		"test.one"		|	"one"
		"a.b.c.d.foo"	|	"foo"
	}

	void setApplicationContext(ApplicationContext applicationContext) {
		this.queue = applicationContext.getBean("in", MessageChannel.class)
	}
}
