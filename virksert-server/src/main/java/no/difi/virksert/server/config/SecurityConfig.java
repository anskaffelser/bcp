package no.difi.virksert.server.config;

import no.difi.virksert.server.security.VirksertAuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author erlend
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private VirksertAuthenticationManager authenticationManager;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // .antMatchers("/api/v1/**", "/webjars/**", "/favicon.ico", "/").permitAll()
                // .antMatchers("/signin", "/signin/*").anonymous()
                // .anyRequest().permitAll()
                // .anyRequest().authenticated()
                // .anyRequest().denyAll()

                .and()
                .formLogin()
                .loginPage("/signin")
                .usernameParameter("participant")
                .passwordParameter("code")
                .defaultSuccessUrl("/")
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/signout")
                .logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationManager);
    }
}
