package vjames.developer.MessConnect.Configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vjames.developer.MessConnect.Utils.AES256;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
    @Value("${spring.datasource.url}")
    private String urlDb;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    
    private final String SECRET_KEY = "Shine123@";
    private final String SALT = "Itadakimasu...1122@!";

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(AES256.decrypt(urlDb, SECRET_KEY, SALT));
        dataSourceBuilder.username(AES256.decrypt(userName, SECRET_KEY, SALT));
        dataSourceBuilder.password(AES256.decrypt(password, SECRET_KEY, SALT));
        return dataSourceBuilder.build();
    }
}
