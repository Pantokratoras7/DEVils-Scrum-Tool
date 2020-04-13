package scrumtool.conf;

import scrumtool.data.DataAccess;

import java.util.Properties;
import java.util.Set;
import java.security.Key;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * A singleton that holds all the configuration items of the web app.
 */
public class Configuration {

    public static final String[] CONFIG_KEYS = new String[]{"x", "y"};
    private static final String KEY = new String("secret_key");
    private static final Configuration self = new Configuration();

    //public static final Key key = MacProvider.generateKey();  //secret key for authentication
    //private static final Key key = ;
    private DataAccess dataAccess = new DataAccess();
    private String contextPath = null;
    private Properties props = new Properties();

    private Configuration() {}

    public static Configuration getInstance() {
        return self;
    }

    void setup(String contextPath, Properties props) throws ConfigurationException {
        this.contextPath = contextPath;
        this.props = props;

        try {
            dataAccess.setup(
                getProperty("db.driver"),
                getProperty("db.url"),
                getProperty("db.user"),
                getProperty("db.pass")
            );
        }
        catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getProperty(String name) {
        return getProperty(name, null);
    }

    public String getProperty(String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    public Set<String> propertyNames() {
        return props.stringPropertyNames();
    }

    public DataAccess getDataAccess() { return dataAccess; }

    public String getKey() { return KEY; }
}
