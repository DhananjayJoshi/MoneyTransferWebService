package com.interview;

import com.interview.dao.AccountDAO;
import com.interview.entity.Account;
import com.interview.resource.AccountResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * http://www.dropwizard.io/1.0.6/docs/manual/core.html#application
 */
public class ServiceApplication extends Application<ServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new ServiceApplication().run(args);
    }

    /**
     * Hibernate bundle.
     */

    private final HibernateBundle<ServiceConfiguration> hibernateBundle = new HibernateBundle<ServiceConfiguration>(Account.class) {

        @Override
        public DataSourceFactory getDataSourceFactory(ServiceConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new FlywayBundle<ServiceConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ServiceConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }

            @Override
            public FlywayFactory getFlywayFactory(ServiceConfiguration configuration) {
                return configuration.getFlywayFactory();
            }
        });

    }

    @Override
    public void run(ServiceConfiguration config, Environment env) throws Exception {

        // cleans database
        config.getFlywayFactory().build(config.getDataSourceFactory().build(env.metrics(), "db")).clean();

        // re-run flyway migrations
        config.getFlywayFactory().build(config.getDataSourceFactory().build(env.metrics(), "db")).migrate();


        // Datasource factory, jdbi connections
        /*final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(env, config.getDataSourceFactory(), "postgresql");*/
        final AccountDAO accountDAO = new AccountDAO(hibernateBundle.getSessionFactory());
        AccountResource accountResource = new AccountResource(accountDAO);

        //register your API resource here
        env.jersey().register(accountResource);
        env.jersey().register(new AccountResource(accountDAO));
    }
}
