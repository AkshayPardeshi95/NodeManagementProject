package com.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter  {

//	@Autowired
//	private UserDetailsService userService;
//	
//	@Bean
//	public DaoAuthenticationProvider authProvidser()
//	{
//		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
//		provider.setUserDetailsService(userService);
//		provider.setPasswordEncoder(new BCryptPasswordEncoder());
//		return provider;
//	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.authorizeRequests()
		.antMatchers("/securedHome").hasRole("ADMIN")
		.antMatchers("/secured").hasRole("DBA")
		.antMatchers("/secured/*").hasRole("DBA")
		.antMatchers("/securedHome/*").hasRole("ADMIN")
		.antMatchers("/h2console").hasRole("DBA")
		.antMatchers("/").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') OR hasRole('ROLE_DBA')")
		.antMatchers("/*").hasRole("USER")
		.anyRequest().authenticated()
		.and().formLogin().loginPage("/login").permitAll().successHandler(myAuthenticationSuccessHandler()).and()
		.logout().invalidateHttpSession(true).clearAuthentication(true).logoutUrl("/logout").logoutSuccessUrl("/login");
		http.headers().frameOptions().disable();
		http.sessionManagement().maximumSessions(10).maxSessionsPreventsLogin(true);
		super.configure(http);
	}
	
	
	    @Bean("authenticationManager")
	    @Override
	    public AuthenticationManager authenticationManagerBean() throws Exception {
	            return super.authenticationManagerBean();
	    }
	 
	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	        // @formatter:off
	        auth.inMemoryAuthentication()
	        .withUser("Admin").password("{noop}EvaiKiO1").roles("ADMIN");
	        auth.inMemoryAuthentication().withUser("User").password("{noop}User@123").roles("USER");
	        auth.inMemoryAuthentication().withUser("Akshay").password("{noop}myDBA@`12").roles("DBA");
	        // @formatter:on
	    }
	 
	    @Bean("AuthenticationSuccessHandler")
	    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
	        return new AuthSuccessHandler();
	    }
	    
	    
}
