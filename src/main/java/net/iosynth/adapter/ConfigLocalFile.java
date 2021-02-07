/**
 * 
 */
package net.iosynth.adapter;

import java.io.File;

/**
 * @author rradev
 *
 */
public class ConfigLocalFile {
    protected File file;
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
	public ConfigLocalFile(){
    	// Adapter default configuration
    	this.file          = null;
        this.clients      = 1;
        this.seed         = 2052703995999047696L; // magic number
    }

    public void setFile(File file) {
        this.file = file;
    }
}
