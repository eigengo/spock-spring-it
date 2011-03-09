package org.spockframework.springintegration.examples.service;

import org.spockframework.springintegration.examples.domain.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author janm
 */
@Service
@Transactional
public class DefaultFooService implements FooService {
	private HibernateTemplate hibernateTemplate;

	@Autowired
	public DefaultFooService(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public int x(String query) {
		Document d = new Document();
		d.setTitle("x");

		this.hibernateTemplate.saveOrUpdate(d);

		return query.length();
	}
}
