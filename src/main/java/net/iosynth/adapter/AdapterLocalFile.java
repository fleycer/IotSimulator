package net.iosynth.adapter;

import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

import java.io.*;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rradev
 *
 */
public class AdapterLocalFile extends Thread {
	// Adapter default configuration
	private File localFile;

	private BlockingQueue<Message> msgQueue;
	private BufferedWriter bw;

    private final Logger logger = Logger.getLogger(AdapterLocalFile.class.getName());

    /**
     * For json deserialization
     * @param cfg
     * @param msgQueue
     */
    public AdapterLocalFile(ConfigLocalFile cfg, BlockingQueue<Message> msgQueue) throws FileNotFoundException {
		// Adapter default configuration
    	this.localFile   = cfg.file;
		setOptions(msgQueue);
		start();
    }
    
	/**
	 * @param msgQueue
	 * @throws FileNotFoundException
	 */
	public void setOptions(BlockingQueue<Message> msgQueue) throws FileNotFoundException {
		this.msgQueue = msgQueue;
		try {
			FileOutputStream fos = new FileOutputStream(localFile);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		} catch (FileNotFoundException me) {
			logger.log(Level.SEVERE, me.toString(), me);
			throw me;
		}
	}

	@Override
	public void run() {
		try {
			long k = 0;
			try {
				while (true) {
					final Message msg = msgQueue.take();
					if (k % 100000 == 0) {
						logger.info("queue: " + msgQueue.size());
					}

					bw.write(msg.getMsg().toString());
					bw.newLine();
					bw.flush();

					k++;
				}
			} catch (InterruptedException ex) {
				logger.log(Level.SEVERE, ex.toString(), ex);
			}

		} catch (IOException me) {
			logger.log(Level.SEVERE, me.toString(), me);
			System.exit(1);
		} finally {

		}

	}
	
}
