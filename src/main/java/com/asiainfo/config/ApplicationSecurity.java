package com.asiainfo.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin().loginPage("/login").failureUrl("/login?error")
				.permitAll().and().logout().permitAll();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
		.withUser("yecl").password("123456").roles("ADMIN", "USER")
		.and()
		.withUser("taohj").password("123456").roles("USER")
		.and()
		.withUser("hequan").password("123456").roles("USER")
		.and()
		.withUser("zhangyx").password("123456").roles("USER")
		.and()
		.withUser("zhaolg").password("123456").roles("USER")
		.and()
		.withUser("liuhong").password("123456").roles("USER")
		.and()
		.withUser("wangxf").password("123456").roles("USER")
		.and()
		.withUser("xinkai").password("123456").roles("USER")
		.and()
		.withUser("zhushuai").password("123456").roles("USER")
		.and()
		.withUser("wangtt").password("123456").roles("USER")
		.and()
		.withUser("yuxm").password("123456").roles("USER")
		.and()
		.withUser("chensy").password("123456").roles("USER")
		.and()
		.withUser("liwei").password("123456").roles("USER");
	}
}
