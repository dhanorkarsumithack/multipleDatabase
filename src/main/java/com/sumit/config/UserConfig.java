package com.sumit.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		entityManagerFactoryRef = "entityManagerFactoryBeanOne",
		basePackages = {"com.sumit.user.repo"},
		transactionManagerRef = "transactionManagerOne"
)
public class UserConfig {
	
	
	@Autowired
	private Environment env;
	
	//Datasource
	@Primary
	@Bean(name = "userDatasource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));	
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
		return dataSource;
	}
	
	//entityManagerFactory
	//LocalContainerEntityManagerFactoryBean--> used to set entitymanagerfactory
	@Primary
	@Bean(name = "entityManagerFactoryBeanOne")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		
		LocalContainerEntityManagerFactoryBean bean=new LocalContainerEntityManagerFactoryBean();
		
		bean.setDataSource(dataSource());
		
		JpaVendorAdapter adapter=new HibernateJpaVendorAdapter();
		bean.setJpaVendorAdapter(adapter);
		
		bean.setPackagesToScan("com.sumit.user.entity");
		
		//set properties in map
		Map<String, String> props=new HashMap<>();
		props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		props.put("hibernate.hbm2ddl-auto", "update");
		props.put("jpa.show-sql","true");
		bean.setJpaPropertyMap(props);
		
		return bean;
	}
	
	//platformTransactionManager
	
	@Primary
	@Bean(name = "transactionManagerOne")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager=new JpaTransactionManager();
		manager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return manager;
	}

}
