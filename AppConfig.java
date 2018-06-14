package com.epam.charity.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.epam.charity.controller.Mappings;
import com.epam.charity.interceptor.validators.LimitStartParamsValidator;
import com.epam.charity.interceptor.validators.SortParamValidator;
import com.epam.charity.interceptor.validators.SupportedLocaleChangeInterceptor;
import com.epam.charity.repository.LangNewsRepository;
import com.epam.charity.repository.NewsRepository;
import com.epam.charity.service.*;
import com.epam.charity.utils.JunitChecker;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Main WebApplication configuration class.
 */

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.epam.charity"})
public class AppConfig extends WebMvcConfigurerAdapter {

    private static final int MB_20 = 20_971_520;
    private static final int MB_1 = 1_048_576;
    private static final int COOKIE_MAX_AGE = 2_592_000;
    public static final String CONFIG_PATH = "config";
    public static final String LOCAL_ENV = "local";
    public static final String DEV_SCHEMATA = "dev";
    private static final Logger logger = Logger.getLogger(WebConfig.class);

    public static final String LOCAL_ENV_MESSAGE =
            "\n\n*************************************\n" + "Server started with local environment.\n"
                    + "Config will be loaded from classpath resource src/main/resources/config/local.conf.\n"
                    + "Please, create this file if it doesn't exist.\n"
                    + "This file is in .gitignore and should not be pushed to git under any circumstances.\n"
                    + "If you see this on the actual server then you need to add -Dschool.env parameter.\n"
                    + "*************************************\n\n";


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*_limit _start param validator*/
        registry.addInterceptor(new LimitStartParamsValidator()).addPathPatterns(Mappings.API + "/**");

        /*_sort param validator*/
        registry.addInterceptor(new SortParamValidator()).addPathPatterns(
                Mappings.ALBUMS + Mappings.PREVIEW, Mappings.EVENTS + Mappings.PREVIEW,
                Mappings.NEWS + Mappings.PREVIEW);

        registry.addInterceptor(new SupportedLocaleChangeInterceptor()).addPathPatterns(Mappings.LOCALE + Mappings.NEWS + Mappings.EVENTS);
//        registry.addInterceptor(new SupportedLocaleChangeInterceptor()).addPathPatterns(Mappings.LOCALE + Mappings.NEWS + Mappings.EVENTS);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale(SupportedLanguages.UK.name().toLowerCase()));
        resolver.setCookieName("lang");
        resolver.setCookieMaxAge(COOKIE_MAX_AGE);
        return resolver;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HandlerMapping defaultServletHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    /*
     *  Start with -Dschool.env=dev or -Dschool.env=stage
     */
    @Bean
    public static Config config() {

//        if (JunitChecker.isJunitPresent()) {
//            throw new RuntimeException("\n\nJunit jar is found in classpath.\n" + JunitChecker.EVIL + "\n"
//                    + "This bean constructs Config taking into account\n"
//                    + "local environment overrides and should not be used\n" + "for unit testing!!\n"
//                    + "Use special test config which will be consistent\n" + "between environments.\n\n\n");
//        }

        return TypeSafeConfigFactory.getConfig();
    }


    @Bean
    public DBConfig dbConfig(Config config) {
        Config db = config.getConfig("school.db");
        return DBConfig.of(db.getString("driver"), db.getString("url"), db.getString("user"),
                db.getString("password"), db.getString("schema"), db.getInt("max-pool-size"));
    }

    @Bean
    public AuthenticationConfig authenticationConfig(Config config) {
        Config db = config.getConfig("school.security");
        return AuthenticationConfig.of(db.getBoolean("disable"));
    }

    /**
     * DataSource bean declaration
     *
     * @return dataSource bean
     */
    @Bean
    public DataSource dataSource(DBConfig dbConfig) {
        logger.info("Driver " + dbConfig.driver);
        logger.info("Url " + dbConfig.url);
        logger.info("User " + dbConfig.user);
        logger.info("Schema:  " + dbConfig.schema);
        logger.info("Max pool size " + dbConfig.maxPoolSize);

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(dbConfig.driver);
        hikariConfig.setJdbcUrl(dbConfig.url);
        hikariConfig.setUsername(dbConfig.user);
        hikariConfig.setPassword(dbConfig.password);
        hikariConfig.setMaximumPoolSize(dbConfig.maxPoolSize);
        hikariConfig.setPoolName("springHikariCP");

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public TransactionAwareDataSourceProxy transactionAwareDataSourceProxy(DBConfig dbConfig) {
        return new TransactionAwareDataSourceProxy(dataSource(dbConfig));
    }

    @Bean
    public DataSourceTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource(dbConfig(config())));
    }


    @Bean
    public DataSourceConnectionProvider dataSourceConnectionProvider(DBConfig dbConfig) {
        return new DataSourceConnectionProvider(transactionAwareDataSourceProxy(dbConfig));
    }

    @Bean
    public JOOQToSpringExceptionTransformer jooqToSpringExceptionTransformer() {
        return new JOOQToSpringExceptionTransformer();
    }

    @Bean
    public DefaultConfiguration configuration(DBConfig dbConfig) {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();

        jooqConfiguration.set(dataSourceConnectionProvider(dbConfig));
        jooqConfiguration.set(new DefaultExecuteListenerProvider(
                jooqToSpringExceptionTransformer()
        ));

        SQLDialect dialect = SQLDialect.valueOf("POSTGRES");
        jooqConfiguration.set(dialect);

        return jooqConfiguration;
    }

    @Bean
    public DefaultDSLContext dslContext(DBConfig dbConfig) {
        return new DefaultDSLContext(configuration(dbConfig));
    }

//    @Bean
//    public DSLContext dslContext(DataSourceConnectionProvider dataSourceConnectionProvider, DBConfig dbConfig) throws SQLException {
//        Settings settings = new Settings().withRenderMapping(new RenderMapping()
//                .withSchemata(new MappedSchema().withInput(DEV_SCHEMATA).withOutput(dbConfig.schema)));
//
//        return DSL.using(dataSourceConnectionProvider.dataSource().getConnection(), settings);
//    }
//
//  @Bean
//  public DefaultConfiguration

    @Bean(name = "multipartResolver")
    public MultipartResolver getMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(MB_20);
        multipartResolver.setMaxInMemorySize(MB_1);
        return multipartResolver;
    }

    @Bean
    public Cloudinary cloudinary(Config config) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name",
                config.getValue("school.cloudinary.cloud_name").render().replaceAll("\"", ""), "api_key",
                config.getValue("school.cloudinary.api_key").render().replaceAll("\"", ""), "api_secret",
                config.getValue("school.cloudinary.api_secret").render().replaceAll("\"", "")));
        logger.info("Cloudinary Info read was Succesful");
        return cloudinary;

    }

    @Bean
    public CloudinaryService cloudinaryHelper(Cloudinary cloudinary) {
        return new CloudinaryServiceImpl(cloudinary);
    }


    /**
     * Configuration for Swagger2 tool
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebConfigFactory.getWebConfig().getStaticResourceLocation().ifPresent(path -> {
            logger.info("Resource folder '" + path + "' is bound to /**");

            registry.addResourceHandler("/**").addResourceLocations(path.normalize().toUri().toString());
        });

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");


    }


    @lombok.Value(staticConstructor = "of")
    public static class DBConfig {
        String driver;
        String url;
        String user;
        String password;
        String schema;
        int maxPoolSize;
    }

    @lombok.Value(staticConstructor = "of")
    public static class AuthenticationConfig {
        boolean disable;
    }

}