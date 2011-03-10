package org.spockframework.springintegration.web.examples.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author janm
 */
@Service
public class HibernateManagementService implements ManagementService {
	private HibernateTemplate hibernateTemplate;

	@Autowired
	public HibernateManagementService(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@Override
	public void save(Object o) {
		this.hibernateTemplate.saveOrUpdate(o);
	}

	@Override
	public <T> T get(Class<T> type, Serializable id) {
		return this.hibernateTemplate.get(type, id);
	}
}
