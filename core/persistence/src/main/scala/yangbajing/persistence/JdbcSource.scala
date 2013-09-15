package yangbajing.persistence

import org.apache.tomcat.jdbc.pool.{PoolProperties, DataSource}

object JdbcDataSource {

  def tomcat(
              url: String,
              driverClassName: String,
              username: String,
              password: String,
              jmxEnabled: Boolean = true,
              testWhileIdle: Boolean = false,
              testOnBorrow: Boolean = true,
              validationQuery: String = "SELECT 1",
              testOnReturn: Boolean = false,
              validationInterval: Int = 30000,
              timeBetweenEvictionRunsMillis: Int = 30000,
              maxActive: Int = 100,
              initialSize: Int = 10,
              maxWait: Int = 10000,
              removeAbandonedTimeout: Int = 60,
              minEvictableIdelTimeMillis: Int = 30000,
              minIdle: Int = 10,
              logAbandoned: Boolean = true,
              removeAbandoned: Boolean = true,
              isJdbcInterceptors: Boolean = true): DataSource = {

    val p = new PoolProperties()
    p.setUrl(url)
    p.setDriverClassName(driverClassName)
    p.setUsername(username)
    p.setPassword(password)
    p.setJmxEnabled(jmxEnabled)
    p.setTestWhileIdle(testWhileIdle)
    p.setTestOnBorrow(testOnBorrow)
    p.setValidationQuery(validationQuery)
    p.setTestOnReturn(testOnReturn)
    p.setValidationInterval(validationInterval)
    p.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis)
    p.setMaxActive(maxActive)
    p.setInitialSize(initialSize)
    p.setMaxWait(maxWait)
    p.setRemoveAbandonedTimeout(removeAbandonedTimeout)
    p.setMinEvictableIdleTimeMillis(minEvictableIdelTimeMillis)
    p.setMinIdle(minIdle)
    p.setLogAbandoned(logAbandoned)
    p.setRemoveAbandoned(removeAbandoned)

    if (isJdbcInterceptors)
      p.setJdbcInterceptors(
        "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer")

    val datasource = new DataSource()
    datasource.setPoolProperties(p)
    datasource
  }

}
