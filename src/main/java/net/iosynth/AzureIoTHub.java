package net.iosynth;

import com.google.gson.Gson;
import net.iosynth.adapter.*;
import net.iosynth.device.Device;
import net.iosynth.device.DeviceControl;
import net.iosynth.device.DevicesFromJson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 *
 */
public class AzureIoTHub {
	protected List<BlockingQueue<Message>> msgQueues;
	protected AdapterAzureIoTHub iotHub;

	protected AzureIoTHub(Config cfg) {
		// set configuration from Json file
		Gson gson = new Gson();
		ConfigAzureIoTHub cfgIoTHub = gson.fromJson(cfg.cfgJson, ConfigAzureIoTHub.class);
		cfgIoTHub.clients = cfgIoTHub.clients < 1 ? 1: cfgIoTHub.clients;
		
		// Setup clients
		msgQueues = new ArrayList<BlockingQueue<Message>>(cfgIoTHub.clients);
		for(int i=0; i<cfgIoTHub.clients; i++){
			msgQueues.add(new LinkedBlockingQueue<Message>());
		}
		for (BlockingQueue<Message> msgQueue : msgQueues) {
			try {
				iotHub = new AdapterAzureIoTHub(cfgIoTHub, msgQueue);
			} catch (URISyntaxException e) {
				return;
			}
		}
		// Create devices
		long seed = cfgIoTHub.seed;
		DevicesFromJson fromJson = new DevicesFromJson();
		List<Device> devs = fromJson.build(cfg.devJson, seed);
		DeviceControl devControl = new DeviceControl(5);
		int k=0;
		int i=0;
		int devsPerClient = (int)Math.ceil((double)devs.size() / (double)cfgIoTHub.clients);
		for (final Device dev : devs) {
			if(k>devsPerClient){
				k=0;
				i++;
			}
			devControl.addDevice(dev, msgQueues.get(i));
		}

		devControl.forever();
	}
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Config cfg = new Config(args);
		AzureIoTHub app = new AzureIoTHub(cfg);
	}
}
