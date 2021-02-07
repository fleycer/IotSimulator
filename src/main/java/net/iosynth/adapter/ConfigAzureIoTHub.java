/**
 * 
 */
package net.iosynth.adapter;

import java.util.UUID;

/**
 * @author rradev
 *
 */
public class ConfigAzureIoTHub {
    protected String host;
    protected String deviceId;
    protected String accessKey;
    /**
     *
     */
    public int clients;
    /**
     * random generator sees
     */
    public long seed;

	/**
	 *
	 */
	public ConfigAzureIoTHub(){
    	// Adapter default configuration
    	this.host          = "amqp://localhost:5672";
    	this.deviceId      = "iosynth";
    	this.accessKey     = "to-be-defined";
        this.clients      = 1;
        this.seed         = 2052703995999047696L; // magic number
    }

}
