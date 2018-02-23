package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import com.anexa.livechat.enums.WaBoxAppEventType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppRequestDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String EVENT = "event";
	public static final String TOKEN = "token";
	public static final String CHANNEL_PHONE_NUMBER_UID = "uid";

	public static final String CONTACT_TYPE = "contact[type]";
	public static final String CONTACT_NAME = "contact[name]";
	public static final String CONTACT_PHONE_NUMBER_UID = "contact[uid]";

	public static final String MESSAGE_DTM = "message[dtm]";
	public static final String MESSAGE_UID = "message[uid]";
	public static final String MESSAGE_CUID = "message[cuid]";
	public static final String MESSAGE_DIRECTION = "message[dir]";
	public static final String MESSAGE_TYPE = "message[type]";
	public static final String MESSAGE_ACK = "message[ack]";

	public static final String MESSAGE_BODY_TEXT = "message[body][text]";
	public static final String MESSAGE_BODY_IMAGE_CAPTION = "message[body][caption]";
	public static final String MESSAGE_BODY_IMAGE_MIME_TYPE = "message[body][mimetype]";
	public static final String MESSAGE_BODY_IMAGE_URL = "message[body][url]";
	public static final String MESSAGE_BODY_IMAGE_THUMB = "message[body][thumb]";
	public static final String MESSAGE_BODY_IMAGE_SIZE = "message[body][size]";

	private WaBoxAppEventType event;

	private String token;
	
	private String channelPhoneNumber;

	private WaBoxAppContactDto contact;

	private WaBoxAppMessageDto message;
}
