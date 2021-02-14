/**
 * 
 */
package net.iosynth.device;

import java.text.SimpleDateFormat;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.iosynth.adapter.Message;
import net.iosynth.gen.Xoroshiro128;
import net.iosynth.sensor.Sensor;

/**
 * @author rradev
 *
 */
public abstract class Device implements Runnable {
	protected BlockingQueue<Message> msgQueue;
	protected BlockingQueue<Device>  delayQueue;

	//protected SDID      sdid;
	protected String   uuid;
	protected String   ipv4, ipv6;
	protected String   mac48, mac64;
	protected String   topic;
	protected Sampling sampling;
	protected int copy;
	protected double out_of_order;
	protected double message_loss;
	protected volatile List<Message> outOrder; // out of order queue
	protected String json_template;
	protected transient DeviceTemplate deviceTemplate;

	/**
	 * id in the delay devices list
	 */
	protected int delayId;

	protected Sensor sensors[];
	
	protected Xoroshiro128 rnd;
	
	final static protected SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private transient final Logger logger = Logger.getLogger(Device.class.getName());
	
	/**
	 * 
	 */
	public Device() {
		//this.sdid     = new SDIDString();
		this.topic    = new String("topic");
		this.sampling = new SamplingFixed();
		this.copy    = 1;
		this.out_of_order = 0.0;
		this.message_loss = 0.0;
		this.outOrder  = new LinkedList<Message>();
		//this.sensors = new ArrayList<>();
	}

	/**
	 * Check the correctness of instance parameters after deserialization from json
	 */
	abstract public void checkParameters();
	/**
	 * @return Returns list of replicated devices.
	 */
	abstract public List<Device> replicate();
	
/*	*//**
	 * @return the did
	 *//*
	public SDID getSDID() {
		return sdid;
	}*/

/*	*//**
	 * @param sdid 
	 *//*
	public void setSDID(SDID sdid) {
		this.sdid = sdid;
	}*/
	
	
	
	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
/*	*//**
	 * 
	 *//*
	public void buildTopicO(){
		String[] strs = topic.split("\\{\\$sdid}"); // {$sdid}
		StringBuilder b = new StringBuilder(255);
		if(strs.length == 0){
			topic = sdid.getUUID();
		}
		if(strs.length == 1){
			if(strs[0].length() == topic.length()){
				// topic have no parameters
			} else {
				topic = b.append(strs[0]).append(sdid.getUUID()).toString();
			}
		}
		if(strs.length == 2){
			topic = b.append(strs[0]).append(sdid.getUUID()).append(strs[1]).toString();
		}
	}*/
	
	transient private static final String patternUUID  = "\\{\\$uuid}";
	transient private static final String patternIPv4  = "\\{\\$ipv4}";
	transient private static final String patternMAC48 = "\\{\\$mac48}";
	transient private static final String patternMAC64 = "\\{\\$mac64}";
	transient private static final Pattern pUUID  = Pattern.compile(patternUUID);
	transient private static final Pattern pIPv4  = Pattern.compile(patternIPv4);
	transient private static final Pattern pMAC48 = Pattern.compile(patternMAC48);
	transient private static final Pattern pMAC64 = Pattern.compile(patternMAC64);
	/**
	 * 
	 */
	public void buildTopic(){
		topic = pUUID.matcher(topic).replaceAll(uuid);
		topic = pIPv4.matcher(topic).replaceAll(ipv4);
		topic = pMAC48.matcher(topic).replaceAll(mac48);
		topic = pMAC64.matcher(topic).replaceAll(mac64);
	}

	
	
	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the ipv4
	 */
	public String getIpv4() {
		return ipv4;
	}

	/**
	 * @param ipv4 the ipv4 to set
	 */
	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}

	/**
	 * @return the ipv6
	 */
	public String getIpv6() {
		return ipv6;
	}

	/**
	 * @param ipv6 the ipv6 to set
	 */
	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	/**
	 * @return the mac48
	 */
	public String getMac48() {
		return mac48;
	}

	/**
	 * @param mac48 the mac48 to set
	 */
	public void setMac48(String mac48) {
		this.mac48 = mac48;
	}

	/**
	 * @return the mac64
	 */
	public String getMac64() {
		return mac64;
	}

	/**
	 * @param mac64 the mac64 to set
	 */
	public void setMac64(String mac64) {
		this.mac64 = mac64;
	}

	/**
	 * Sets message queue to communicate with adapter. 
	 * @param msgQueue
	 */
	public void setQueue(BlockingQueue<Message> msgQueue){
		this.msgQueue = msgQueue;
	}

	/**
	 * @return internal message queue
	 */
	public BlockingQueue<Message> getQueue(){
		return msgQueue;
	}
	
	/**
	 * Sets delay queue for variable rate devices. 
	 * @param delayQueue
	 */
	public void setDelayQueue(BlockingQueue<Device> delayQueue){
		this.delayQueue = delayQueue;
	}

	/**
	 * @return internal delay queue
	 */
	public BlockingQueue<Device> getDelayQueue(){
		return delayQueue;
	}

	/**
	 * @return the sampling
	 */
	public Sampling getSampling() {
		return sampling;
	}

	/**
	 * @param sampling the sampling to set
	 */
	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}
	
	/**
	 * @return the deviceCopy
	 */
	public int getDeviceCopy() {
		return copy;
	}

	/**
	 * @param copy
	 */
	public void setDeviceCopy(int copy) {
		this.copy = copy;
	}
	
	/**
	 * @return List of sensors
	 */
	public Sensor[] getSensors() {
		return sensors;
	}

	/**
	 * @return the rnd
	 */
	public Xoroshiro128 getRnd() {
		return rnd;
	}

	/**
	 * @param rnd the rnd to set
	 */
	public void setRnd(Xoroshiro128 rnd) {
		this.rnd = rnd;
		for(final Sensor sen: sensors){
			sen.setRnd(rnd);
		}
	}
	
	/**
	 * @return the json_template
	 */
	public String getJson_template() {
		return json_template;
	}

	/**
	 * @param json_template the json_template to set
	 */
	public void setJson_template(String json_template) {
		this.json_template = json_template;
	}

	/**
	 * @return the deviceTemplate
	 */
	public DeviceTemplate getDeviceTemplate() {
		return deviceTemplate;
	}

	/**
	 * @param deviceTemplate the deviceTemplate to set
	 */
	public void setDeviceTemplate(DeviceTemplate deviceTemplate) {
		this.deviceTemplate = deviceTemplate;
	}

	
	/**
	 * @return the delayId
	 */
	public int getDelayId() {
		return delayId;
	}

	/**
	 * @param delayId the delayId to set
	 */
	public void setDelayId(int delayId) {
		this.delayId = delayId;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		for(final Sensor sensor : sensors) {
			if(message_loss > 0.00000001 && rnd.nextDouble() < message_loss) {
				sensor.step(2);
			} else {
				sensor.step(1);
			}
		}

		if(out_of_order > 0.00000001 && rnd.nextDouble() < out_of_order){
			outOrder.add(toJsonMessage()); // out of order queue
		} else {
			if(outOrder.size() > 10 || (rnd.nextDouble() < 0.1 && outOrder.size() > 0)){
				getQueue().add(outOrder.remove(0)); // too long time or too many are delayed
			} else { 
				getQueue().add(toJsonMessage()); // normal case
			}
		}
		
		if (sampling.getClass() != SamplingFixed.class) {
			getDelayQueue().add(this);
		}
	}
	
	
	/**
	 * @return json message
	 */
	public Message toJsonMessage(){
		if(deviceTemplate == null){
			return toJsonListMessage();
		} else {
			System.out.println(getUuid());
			return new Message(getTopic(), getUuid(), getIpv4(), getIpv6(), getMac48(), getMac64(), getDeviceTemplate().getJson(sensors));
		}
	}
	
	/**
	 * @return Json representation of sensor data
	 */
	public Message toJsonListMessage(){
		// TODO: performance improvement
		StringBuilder m = new StringBuilder();

		m.append("{");
		for (final Sensor sensor : sensors) {
			try {
				String name = sensor.getName();
				m.append("\"").append(name).append("\":").append(sensor.getString()).append(",");
			} catch (IllegalFormatException e) {
				logger.severe("Illegal sensor format: " + sensor.getName() + " " + sensor.getFormat());
			}
		}
		m.deleteCharAt(m.length() - 1); // remove last comma
		m.append("}");

		System.out.println(getUuid());

		return new Message(getTopic(), getUuid(), getIpv4(), getIpv6(), getMac48(), getMac64(), m.toString());
	}
	
}
