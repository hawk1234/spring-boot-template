package com.mz.example.api;

import com.mz.example.config.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String USER_NAME = "user";
    private static final String ADMIN_NAME = "admin";

    private static final String USER_ROLE_NAME = "USER";
    private static final String ADMIN_ROLE_NAME = "ADMIN";

    static final String USER_ROLE = "ROLE_"+USER_ROLE_NAME;
    static final String ADMIN_ROLE = "ROLE_"+ADMIN_ROLE_NAME;

    private static final String REALM = "APPLICATION";//TODO: change this to your realm

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth, ApplicationConfig config) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.withUsername(USER_NAME).password(encoder.encode(config.getUserpass())).roles(USER_ROLE_NAME).build();
        UserDetails admin = User.withUsername(ADMIN_NAME).password(encoder.encode(config.getAdminpass())).roles(USER_ROLE_NAME, ADMIN_ROLE_NAME).build();
        auth.inMemoryAuthentication().passwordEncoder(encoder).withUser(user);
        auth.inMemoryAuthentication().passwordEncoder(encoder).withUser(admin);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
                .antMatchers(APIRequestMapping.BASE_PATH+"**").permitAll()
            .and().httpBasic().realmName(REALM).authenticationEntryPoint((request, response, authException) -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            })
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    //<editor-fold desc="Allows user methods to be called for web browser (GET and POST) by default disabled in application.properties.">
    @Bean
    @Scope(value = "singleton")
    public AllowedOriginsAspect allowedOriginsAspect(){
        return new AllowedOriginsAspect();
    }

    @Bean
    @Autowired
    public WebMvcConfigurer corsConfigurer(ApplicationConfig config) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                if(config.hasDefinedOrigins()) {
                    Stream.of(APIRequestMapping.getUserMethods()).forEach(methodPath ->
                            registry.addMapping(methodPath)
                                    .allowedOrigins(config.getAllowedOrigins().toArray(new String[]{}))
                                    .allowCredentials(false)
                                    .allowedMethods(RequestMethod.GET.name(), RequestMethod.POST.name())
                    );
                }
            }
        };
    }
    //</editor-fold>
}
