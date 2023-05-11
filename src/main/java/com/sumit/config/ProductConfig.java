package com.sumit.config;

import java.util.HashMap;
import java.util.Map;

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
		entityManagerFactoryRef = "entityManagerFactoryBeanTwo",
		basePackages = {"com.sumit.product.repo"},
		transactionManagerRef = "transactionManagerTwo"
)
public class ProductConfig {
	
	@Autowired
	private Environment env;
	
	//Datasource
	@Primary
	@Bean(name = "productDataSource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource=new DriverManagerDataSource();
		dataSource.setUrl(env.getProperty("secondary.datasource.url"));	
		dataSource.setDriverClassName(env.getProperty("secondary.datasource.driver-class-name"));
		dataSource.setUsername(env.getProperty("secondary.datasource.username"));
		dataSource.setPassword(env.getProperty("secondary.datasource.password"));
		return dataSource;
	}
	
	//entityManagerFactory
	//LocalContainerEntityManagerFactoryBean--> used to set 
	@Primary
	@Bean(name = "entityManagerFactoryBeanTwo")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		
		LocalContainerEntityManagerFactoryBean bean=new LocalContainerEntityManagerFactoryBean();
		
		bean.setDataSource(dataSource());
		
		JpaVendorAdapter adapter=new HibernateJpaVendorAdapter();
		bean.setJpaVendorAdapter(adapter);
		
		bean.setPackagesToScan("com.sumit.product.entity");
		
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
	@Bean(name = "transactionManagerTwo")
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager manager=new JpaTransactionManager();
		manager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		return manager;
	}
}
