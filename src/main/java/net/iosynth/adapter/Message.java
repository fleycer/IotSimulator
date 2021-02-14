package net.iosynth.adapter;

/**
 * @author rradev
 *
 */
public class Message {
	private String topic;
	private String uuid;
	protected String   ipv4, ipv6;
	protected String   mac48, mac64;
	private String msg;
	
	/**
	 * @param topic
	 * @param msg
	 */
	public Message(String topic, String msg){
		this.topic  = topic;
		this.msg = msg;
	}

	/**
	 * @param topic
	 * @param msg
	 */
	public Message(String topic, String uuid, String ipv4, String ipv6, String mac48, String mac64, String msg){
		this.topic  = topic;
		this.uuid = uuid;
		this.ipv4 = ipv4;
		this.ipv6 = ipv6;
		this.mac48 = mac48;
		this.mac64 = mac64;
		this.msg = msg;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIpv4() {
		return ipv4;
	}

	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}

	public String getIpv6() {
		return ipv6;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	public String getMac48() {
		return mac48;
	}

	public void setMac48(String mac48) {
		this.mac48 = mac48;
	}

	public String getMac64() {
		return mac64;
	}

	public void setMac64(String mac64) {
		this.mac64 = mac64;
	}

	/**
	 * @return message topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * @return message
	 */
	public String getMsg() {
		return msg;
	}


	/**
	 * @param msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Message{" +
				"uuid='" + uuid + '\'' +
				", ipv4='" + ipv4 + '\'' +
				", ipv6='" + ipv6 + '\'' +
				", mac48='" + mac48 + '\'' +
				", mac64='" + mac64 + '\'' +
				", msg='" + msg + '\'' +
				'}';
	}

}
