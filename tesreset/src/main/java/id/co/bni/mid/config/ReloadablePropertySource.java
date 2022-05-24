package id.co.bni.mid.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

public class ReloadablePropertySource extends PropertySource  {
    PropertiesConfiguration propertiesConfiguration;

    public ReloadablePropertySource(String name, PropertiesConfiguration propertiesConfiguration) {
        super(name);
        this.propertiesConfiguration = propertiesConfiguration;
    }

    public ReloadablePropertySource(String name, String path) throws ConfigurationException {
        super(StringUtils.isEmpty(name) ? path : name);
        try {
            this.propertiesConfiguration = new PropertiesConfiguration(path);
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            strategy.setRefreshDelay(1000);
            this.propertiesConfiguration.setReloadingStrategy(strategy);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Object getProperty(String s) {
        return propertiesConfiguration.getProperty(s);
    }
}
