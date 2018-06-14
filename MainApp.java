package com.epam.charity.jetty;

import com.epam.charity.auth.filters.ExceptionHandlerFilter;
import com.epam.charity.config.AppConfig;

import com.epam.charity.config.WebConfig;
import com.epam.charity.config.WebConfigFactory;
import lombok.extern.log4j.Log4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;

@Log4j
public class MainApp {

  private static final int MB_30 = 31_457_280;

  public static void main(String[] args) throws Exception {


    /*App context*/
    final AnnotationConfigWebApplicationContext applicationContext =
        new AnnotationConfigWebApplicationContext();
    ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();

    log.info(
        "Active Profile(s) >>> " + Arrays.toString(configurableEnvironment.getActiveProfiles()));

    applicationContext.register(AppConfig.class);

    final ServletHolder servletHolder =
        new ServletHolder(new DispatcherServlet(applicationContext));
    final ServletContextHandler context = new ServletContextHandler();

    context.setMaxFormContentSize(MB_30);
    context.setErrorHandler(null); // use Spring exception handler(s)
    context.setContextPath("/");
    context.addServlet(servletHolder, "/");

    /*weird jetty does not work with AbstractSecurityWebApplicationInitializer realization*/
    context.addFilter(
        new FilterHolder(new DelegatingFilterProxy(
            AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)),
        "/*", EnumSet.allOf(DispatcherType.class));

    /*ServletContextHandler [...]construction of a context with ServletHandler and optionally session and security handlers [...]*/
    context.setSessionHandler(new SessionHandler());

    /*manually getting WebConfig*/
    WebConfig webConfig = WebConfigFactory.getWebConfig();

    final Server server = new Server(webConfig.getPort());
    server.setHandler(context);

    log.info("Starting server on port " + webConfig.getPort());

    server.start();
    server.join();
  }
}
