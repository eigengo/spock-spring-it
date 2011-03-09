package org.spockframework.springintegration.examples.service;

import org.spockframework.springintegration.examples.domain.Document;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

/**
 * @author janm
 */
public class SomeService {
	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<Integer> idolFind(String query) {
		Document d = new Document();
		d.setId(1L + query.length());
		d.setTitle(query);
		this.entityManager.persist(d);

		return Collections.emptyList();
	}

}
