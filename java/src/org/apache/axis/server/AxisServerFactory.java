package org.apache.axis.server;

import org.apache.axis.ConfigurationProvider;
import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.configuration.FileProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper class for obtaining AxisServers, which hides the complexity
 * of JNDI accesses, etc.
 *
 * !!! QUESTION : Does this class need to play any ClassLoader tricks?
 * 
 * @author Glen Daniels (gdaniels@macromedia.com)
 */ 

public class AxisServerFactory {
    private static FileProvider defaultConfigProvider =
                           new FileProvider(Constants.SERVER_CONFIG_FILE);

    /**
     * Obtain an AxisServer reference, using JNDI if possible, otherwise
     * creating one using the standard Axis configuration pattern.  If we
     * end up creating one and do have JNDI access, bind it to the passed
     * name so we find it next time.
     *
     * @param name the JNDI name we're interested in
     * @param configProvider a ConfigurationProvider which should be used
     *                       to configure any engine we end up creating, or
     *                       null to use the default configuration pattern.
     */
    static public AxisServer getServer(String name,
                                       ConfigurationProvider configProvider)
        throws AxisFault
    {
        AxisServer server = null;
        InitialContext context = null;

        // First, and foremost, if a configProvider is passed in
        //  ALWAYS use that...
        if (configProvider != null) {
            return createNewServer(configProvider);
        }

        // First check to see if JNDI works
        // !!! Might we need to set up context parameters here?
        try {
            context = new InitialContext();
        } catch (NamingException e) {
        }
        
        if (context != null) {
            // We've got JNDI, so try to find an AxisServer at the
            // specified name.
            try {
                server = (AxisServer)context.lookup(name);
            } catch (NamingException e) {
                // Didn't find it.
                server = createNewServer(configProvider);
                try {
                    context.bind(name, server);
                } catch (NamingException e1) {
                    // !!! Couldn't do it, what should we do here?
                }
            }
        } else {
            server = createNewServer(configProvider);
        }

        return server;
    }

    /**
     * Do the actual work of creating a new AxisServer, using the passed
     * configuration provider, or going through the default configuration
     * steps if null is passed.
     *
     * @return a shiny new AxisServer, ready for use.
     */
    static private AxisServer createNewServer(ConfigurationProvider provider)
    {
        // Just use the passed provider if there is one.
        if (provider != null) {
            return new AxisServer(provider);
        }

        // Default configuration steps...
        //
        // 1. Check for a system property telling us which Configuration
        //    Provider to use.  If we find it, try creating one.
        String configClass = System.getProperty("axis.configProviderClass");
        if (configClass != null) {
            // Got one - so try to make it (which means it had better have
            // a default constructor - may make it possible later to pass in
            // some kind of environmental parameters...)
            try {
                Class cls = Class.forName(configClass);
                provider = (ConfigurationProvider)cls.newInstance();
            } catch (ClassNotFoundException e) {
                // Fall through???
            } catch (InstantiationException e) {
                // Fall through???
            } catch (IllegalAccessException e) {
                // Fall through???
            }
        }

        // 2. If we couldn't make one above, use the default one.
        // !!! May want to add options here for getting another system
        //     property which is the config file name...
        if (provider == null) {
            provider = defaultConfigProvider;
        }

        // 3. Create an AxisServer using the appropriate provider
        return new AxisServer(provider);
    }
}
