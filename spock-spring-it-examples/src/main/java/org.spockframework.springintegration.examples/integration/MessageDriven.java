package org.spockframework.springintegration.examples.integration;

import org.spockframework.springintegration.examples.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author janm
 */
@Service
public class MessageDriven {
	private HibernateTemplate hibernateTemplate;

	@Autowired
	public MessageDriven(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@ServiceActivator(inputChannel = "in")
	public void onMessage(@Payload String text) {
		Message message = new Message();
		message.setSourceText(text);
		message.setProcessedText(StringUtils.unqualify(text));
		this.hibernateTemplate.saveOrUpdate(message);
	}
}
