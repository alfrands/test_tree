package id.co.bni.mid.config;

import id.co.bni.mid.model.ResponseDictionary;
import id.co.bni.mid.model.StatusKartuDictionary;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = {"id.co.bni.mid", "id.co.bni.mid.httpBuilder"})
@PropertySource("file:./config/app.properties")
public class Config {

    @Autowired
    private Environment env;

    public String getProperty(String configKey){
        return env.getProperty(configKey);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.config.location", matchIfMissing = false)
    public PropertiesConfiguration propertiesConfiguration(
            @Value("${spring.config.location}") String path) throws Exception {
        String filePath = new File(path.substring("file:".length())).getCanonicalPath();
        PropertiesConfiguration configuration = new PropertiesConfiguration(
                new File(filePath));
        FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
        reloadingStrategy.setRefreshDelay(1000);
        configuration.setReloadingStrategy(reloadingStrategy);
        return configuration;
    }

    @Bean
    public InternalResourceViewResolver defaultViewResolver() {
        return new InternalResourceViewResolver();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        ConnectionPool okHttpConnectionPool = new ConnectionPool(100, 30, TimeUnit.SECONDS);
        builder.connectionPool(okHttpConnectionPool);

        builder.connectTimeout(13, TimeUnit.SECONDS);
        builder.readTimeout(13, TimeUnit.SECONDS);
        builder.writeTimeout(13, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);

        restTemplate.setRequestFactory(new OkHttp3ClientHttpRequestFactory(builder.build()));
        return restTemplate;
    }

    @Bean
    public List<ResponseDictionary> responseDictionaryList() {
        List<ResponseDictionary> responseDictionary = new ArrayList<>();
        try {
            LineIterator it = FileUtils.lineIterator(new File("config/respmessage.settings"));
            List<ResponseDictionary> list = new ArrayList<>();
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] parse = line.split("\\|");

                String appMessage = "";
                if (parse.length > 2) {
                    appMessage = parse[2];
                }

                ResponseDictionary respValue = new ResponseDictionary(parse[0], parse[1], appMessage);
                list.add(respValue);
            }
            responseDictionary.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseDictionary;
    }

    @Bean
    public List<StatusKartuDictionary> statusKartuDictionaryList() {
        List<StatusKartuDictionary> statusKartuList = new ArrayList<>();
        try {
            LineIterator it = FileUtils.lineIterator(new File("config/statuskartu.settings"));
            List<StatusKartuDictionary> list = new ArrayList<>();
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] parse = line.split("\\|");

                String appMessage = "";
                if (parse.length > 4) {
                    appMessage = parse[4];
                }

                StatusKartuDictionary respValue = new StatusKartuDictionary(parse[0], parse[1], parse[2], parse[3], appMessage);
                list.add(respValue);
            }
            statusKartuList.addAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return statusKartuList;
    }
}
