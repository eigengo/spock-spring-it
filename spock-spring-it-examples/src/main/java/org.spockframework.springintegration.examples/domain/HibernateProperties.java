package org.spockframework.springintegration.examples.domain;

import java.util.Properties;

/**
 * @author janm
 */
public class HibernateProperties {


	public Properties asProperties() {
		Properties props = new Properties();
		props.put("hibernate.show_sql", "true");
		props.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		props.put("hibernate.query.factory_class", "org.hibernate.hql.ast.ASTQueryTranslatorFactory");
		props.put("hibernate.cache.use_structured_entries", "true");
		props.put("hibernate.hbm2ddl.auto", "create-drop");
		return props;
	}
}
