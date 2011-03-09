package org.spockframework.springintegration;

import java.util.Map;

/**
 * @author janm
 */
public class CustomJndiBuilder implements JndiBuilder {

	public void build(Map<String, Object> environment) throws Exception {
		environment.put("java:comp/env/strings/jan", "Jan");
	}
}
