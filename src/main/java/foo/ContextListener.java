package foo;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import com.atomikos.icatch.jta.UserTransactionManager;


public class ContextListener implements ServletContextListener {
    
    private UserTransactionManager utm;
	private String appName = "mytransaction: ";

    public void contextInitialized(ServletContextEvent event) {
        try {
            utm = new UserTransactionManager();
            utm.init();
            System.out.println(appName + "initialized transaction manager");
        }
        catch (Exception ex) {
            utm = null;
            throw new RuntimeException(appName + "cannot initialize UserTransactionManager", ex);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (utm != null) {
            utm.close();
            System.out.println(appName + "shut down transaction manager");
        }
    }
}
