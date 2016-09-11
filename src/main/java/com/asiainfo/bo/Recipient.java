package com.asiainfo.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.util.StringUtils;

public class Recipient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4993716536072620046L;

	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String text;
	private boolean toAll;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isToAll() {
		return toAll;
	}

	public void setToAll(boolean toAll) {
		this.toAll = toAll;
	}

	private static final InternetAddress[] addresses = new InternetAddress[0];

	public InternetAddress[] to() throws AddressException {
		return toAddresses(to);
	}

	public InternetAddress[] cc() throws AddressException {
		return toAddresses(cc);
	}

	public InternetAddress[] bcc() throws AddressException {
		return toAddresses(bcc);
	}

	private InternetAddress[] toAddresses(String source) throws AddressException {
		Set<InternetAddress> addressSet = new HashSet<InternetAddress>();
		if (StringUtils.hasText(source)) {
			String[] sourcees = source.split(";");
			for (String _source : sourcees) {
				InternetAddress address = new InternetAddress(_source);
				addressSet.add(address);
			}
		}
		return addressSet.toArray(addresses);
	}

}
