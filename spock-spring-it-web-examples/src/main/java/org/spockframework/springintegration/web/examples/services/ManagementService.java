package org.spockframework.springintegration.web.examples.services;

import java.io.Serializable;

/**
 * @author janm
 */
public interface ManagementService {

	void save(Object o);

	<T> T get(Class<T> type, Serializable id);
}
