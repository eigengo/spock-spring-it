package org.spockframework.springintegration;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.mock.jndi.SimpleNamingContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import javax.sql.XADataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Spock extension that processes the {@link Jndi} annotation and sets up the JNDI environment according to the
 * values in the annotation. You can use the extension on its own, though typically, you'd combine it with the
 * spock-spring extension. This will allow you to write Spock code that looks like this:
 * <code><pre>
 * 	&#64;Jndi(
 * 		dataSources = &#64;DataSource(...)
 * 	)
 * 	&#64;ContextConfiguration(locations = "classpath*:/META-INF/spring/module-context.xml")
 * 	class FooServiceTest extends Specification {
 * 		&#64;Autowired
 * 	 	FooService service
 * 	 	def "some test"() {
 *		}
 *	}
 * </pre></code>
 *
 * @author janm
 */
public class JndiExtension implements IGlobalExtension {

	public void visitSpec(SpecInfo spec) {
		final Jndi jndi = AnnotationUtils.findAnnotation(spec.getReflection(), Jndi.class);
		if (jndi == null) return;

		try {
			NamingContextBuilder builder = NamingContextBuilder.emptyActivatedContextBuilder();

			buildDataSources(builder, jndi.dataSources());
			buildMailSessions(builder, jndi.mailSessions());
			buildTransactionManagers(builder, jndi.transactionManager());
			buildBeans(builder, jndi.beans());
			buildJms(builder, jndi.jms());

			buildCustom(builder, jndi.builder());

		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	private void buildJms(NamingContextBuilder builder, Jms[] jmses) {
		for (Jms jms : jmses) {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
			builder.bind(jms.connectionFactoryName(), factory);
			for (Queue queue : jms.queues()) {
				ActiveMQQueue q = new ActiveMQQueue();
				q.setPhysicalName("queue" + queue.hashCode());
				builder.bind(queue.name(), q);
			}
			for (Topic topic : jms.topics()) {
				ActiveMQTopic t = new ActiveMQTopic();
				t.setPhysicalName("topic" + topic.hashCode());
				builder.bind(topic.name(), t);
			}
		}
	}

	private void buildTransactionManagers(NamingContextBuilder builder, TransactionManager[] transactionManagers) {
		if (transactionManagers.length == 0) return;
		if (transactionManagers.length > 1)
			throw new EnvironmentCreationException("Cannot have more than one TransactionManager");
		TransactionManager transactionManager = transactionManagers[0];
		builder.bind(transactionManager.name(), new UserTransactionManager());
	}

	private void buildCustom(NamingContextBuilder builder, Class<? extends JndiBuilder> builderClass) {
		final JndiBuilder jndiBuilder = instantiate(builderClass);
		Map<String, Object> environment = new HashMap<String, Object>();
		try {
			jndiBuilder.build(environment);
		} catch (Exception e) {
			throw new EnvironmentCreationException(e);
		}
		for (Map.Entry<String, Object> entry : environment.entrySet()) {
			builder.bind(entry.getKey(), entry.getValue());
		}
	}

	private void buildBeans(NamingContextBuilder builder, Bean[] beans) {
		for (Bean bean : beans) {
			Object o = instantiate(bean.type());

			builder.bind(bean.name(), o);
		}
	}

	private <T> T instantiate(Class<T> type) {
		try {
			final Constructor<T> constructor = type.getConstructor();
			ReflectionUtils.makeAccessible(constructor);
			return constructor.newInstance();
		} catch (NoSuchMethodException e) {
			throw new EnvironmentCreationException(e);
		} catch (InvocationTargetException e) {
			throw new EnvironmentCreationException(e);
		} catch (InstantiationException e) {
			throw new EnvironmentCreationException(e);
		} catch (IllegalAccessException e) {
			throw new EnvironmentCreationException(e);
		}
	}

	private void buildMailSessions(NamingContextBuilder builder, MailSession[] mailSessions) {
		for (MailSession mailSession : mailSessions) {
			Properties props = new Properties();
			for (String property : mailSession.properties()) {
				int i = property.indexOf("=");
				if (i == -1) continue;
				props.setProperty(property.substring(0, i), property.substring(i + 1));
			}
			Session session = Session.getInstance(props);

			builder.bind(mailSession.name(), session);
		}

	}

	private void buildDataSources(NamingContextBuilder builder, DataSource[] dataSources) {
		for (DataSource dataSource : dataSources) {
			boolean xa = false;
			for (Class<?> intf : ClassUtils.getAllInterfacesForClass(dataSource.driverClass())) {
				if (intf == XADataSource.class) {
					xa = true;
					break;
				}
			}

			javax.sql.DataSource ds;
			if (xa) {
				AtomikosDataSourceBean realDs = new AtomikosDataSourceBean();
				realDs.setXaDataSourceClassName(dataSource.driverClass().getName());
				Properties p = new Properties();
				p.setProperty("user", dataSource.username());
				p.setProperty("password", dataSource.password());
				p.setProperty("URL", dataSource.url());
				realDs.setXaProperties(p);
				realDs.setUniqueResourceName(dataSource.driverClass().getName() + System.currentTimeMillis());
				realDs.setPoolSize(5);
				ds = realDs;
			} else {
				AtomikosNonXADataSourceBean realDs = new AtomikosNonXADataSourceBean();
				realDs.setDriverClassName(dataSource.driverClass().getName());
				realDs.setUrl(dataSource.url());
				realDs.setUser(dataSource.username());
				realDs.setPassword(dataSource.password());
				realDs.setUniqueResourceName(dataSource.driverClass().getName() + System.currentTimeMillis());
				realDs.setPoolSize(5);
				ds = realDs;
			}

			builder.bind(dataSource.name(), ds);
		}
	}

	private static class NamingContextBuilder implements InitialContextFactoryBuilder {

		/**
		 * An instance of this class bound to JNDI
		 */
		private static NamingContextBuilder activated;

		/**
		 * If no SimpleNamingContextBuilder is already configuring JNDI, createOrUpdate and activate one. Otherwise take the existing activate
		 * SimpleNamingContextBuilder, clear it and return it.
		 * <p/>
		 * This is mainly intended for test suites that want to reinitialize JNDI bindings from scratch repeatedly.
		 *
		 * @return an empty SimpleNamingContextBuilder that can be used to control JNDI bindings
		 * @throws javax.naming.NamingException .
		 */
		public static NamingContextBuilder emptyActivatedContextBuilder()
				throws NamingException {
			if (activated != null) {
				// Clear already activated context builder.
				// activated.clear();
			} else {
				// Create and activate new context builder.
				NamingContextBuilder builder = new NamingContextBuilder();
				// The activate() call will cause an assigment to the activated
				// variable.
				builder.activate();
			}
			return activated;
		}

		private final Hashtable<String, Object> boundObjects = new Hashtable<String, Object>();

		/**
		 * Register the context builder by registering it with the JNDI NamingManager. Note that once this has been done,
		 * <code>new InitialContext()</code> will always return a context from this factory. Use the <code>emptyActivatedContextBuilder()</code>
		 * static method to get an empty context (for example, in test methods).
		 *
		 * @throws IllegalStateException		if there's already a naming context builder registeredwith the JNDI NamingManager
		 * @throws javax.naming.NamingException .
		 */
		public void activate() throws IllegalStateException, NamingException {
			if (!NamingManager.hasInitialContextFactoryBuilder())
				NamingManager.setInitialContextFactoryBuilder(this);
			activated = this;
		}

		/**
		 * Clear all bindings in this context builder.
		 */
		public void clear() {
			for (Map.Entry<String, Object> e : this.boundObjects.entrySet()) {
				if (e.getValue() instanceof AbstractDataSourceBean) {
					((AbstractDataSourceBean)e.getValue()).close();
				}
				if (e.getValue() instanceof UserTransactionManager) {
					((UserTransactionManager)e.getValue()).close();
				}
			}
			this.boundObjects.clear();
		}

		/**
		 * Bind the given object under the given name, for all naming contexts that this context builder will generate.
		 *
		 * @param name the JNDI name of the object (e.g. "java:comp/env/jdbc/myds")
		 * @param obj  the object to bind (e.g. a DataSource implementation)
		 */
		public void bind(String name, Object obj) {
			if (this.boundObjects.contains(name)) return;
			this.boundObjects.put(name, obj);
		}

		/**
		 * Indicates whether we have at least one XA data source
		 *
		 * @return {@code true} if we have XA objects bound
		 */
		boolean isXa() {
			for (Map.Entry<String, Object> e : this.boundObjects.entrySet()) {
				if (e.getValue() instanceof XADataSource) return true;
			}

			return false;
		}

		<T> T getSingleObject(Class<T> clazz) {
			int count = 0;
			T object = null;
			for (Map.Entry<String, Object> e : this.boundObjects.entrySet()) {
				if (clazz.isAssignableFrom(e.getValue().getClass())) {
					if (count > 0) throw new RuntimeException("More than one object of type " + clazz + " found.");
					object = (T) e.getValue();
					count++;
				}
			}
			if (object == null) throw new RuntimeException("No object of type " + clazz + " found.");

			return object;
		}

		/**
		 * Simple InitialContextFactoryBuilder implementation, creating a new SimpleNamingContext instance.
		 *
		 * @see SimpleNamingContext
		 */
		public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) {
			return new InitialContextFactory() {
				@SuppressWarnings("unchecked")
				public Context getInitialContext(Hashtable environment) {
					return new SimpleNamingContext("", NamingContextBuilder.this.boundObjects, environment);
				}
			};
		}

		boolean contains(String name) {
			return this.boundObjects.contains(name);
		}
	}

}
