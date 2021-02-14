package net.iosynth.adapter;

import com.microsoft.azure.sdk.iot.device.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rradev
 *
 */
public class AdapterAzureIoTHub extends Thread {
	// Adapter default configuration
	private String host;
	private String deviceId;
	private String accessKey;

	private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static DeviceClient client;
	private BlockingQueue<Message> msgQueue;

    private final Logger logger = Logger.getLogger(AdapterAzureIoTHub.class.getName());

    /**
     * For json deserialization
     * @param cfg
     * @param msgQueue
     */
    public AdapterAzureIoTHub(ConfigAzureIoTHub cfg, BlockingQueue<Message> msgQueue) throws URISyntaxException {
		// Adapter default configuration
    	this.host   = cfg.host;
    	this.deviceId = cfg.deviceId;
		this.accessKey = cfg.accessKey;
		setOptions(msgQueue);
		start();
    }
    
	/**
	 * @param msgQueue
	 * @throws URISyntaxException
	 */
	public void setOptions(BlockingQueue<Message> msgQueue) throws URISyntaxException {
		this.msgQueue = msgQueue;
		try {
			String connString = "HostName=" + this.host + ";DeviceId=" + this.deviceId +  ";SharedAccessKey=" + this.accessKey;

			// Connect to the IoT hub.
			client = new DeviceClient(connString, protocol);

		} catch (URISyntaxException me) {
			logger.log(Level.SEVERE, me.toString(), me);
			throw me;
		}
	}

	// Print the acknowledgement received from IoT Hub for the telemetry message sent.
	private static class EventCallback implements IotHubEventCallback {
		public void execute(IotHubStatusCode status, Object context) {
			System.out.println("IoT Hub responded to message with status: " + status.name());

			if (context != null) {
				synchronized (context) {
					context.notify();
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			logger.info("Connecting to: " + host + " deviceID: " + deviceId);
			client.open();
			logger.info("Connected");
			long k = 0;
			try {
				while (true) {
					final Message msg = msgQueue.take();
					if (k % 100000 == 0) {
						logger.info("queue: " + msgQueue.size());
					}

					com.microsoft.azure.sdk.iot.device.Message iothubMsg = new com.microsoft.azure.sdk.iot.device.Message(msg.getMsg().getBytes());
					Object lockobj = new Object();

					iothubMsg.setMessageId(UUID.randomUUID().toString());
					if (msg.getUuid() != null)
						iothubMsg.setProperty("uuid", msg.getUuid());

					//
					logger.info("Sending message : " + msg);

					// Send the message.
					EventCallback callback = new EventCallback();
					client.sendEventAsync(iothubMsg, callback, lockobj);
					synchronized (lockobj) {
						try {
							lockobj.wait();
						} catch (InterruptedException e) {
							logger.log(Level.SEVERE,"Error sending event[" + msg.getMsg().toString() + "] to Azure IoT Hub", e);
						}
					}
					k++;
				}
			} catch (InterruptedException ex) {
				logger.log(Level.SEVERE, ex.toString(), ex);
			}

			client.closeNow();
			logger.info("Disconnected");
		} catch (IOException me) {
			logger.log(Level.SEVERE, me.toString(), me);
			System.exit(1);
		} finally {

		}

	}
	
}
