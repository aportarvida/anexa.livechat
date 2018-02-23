package com.anexa.livechat.service.api.waboxapp;

public interface WaBoxAppService {

	boolean sendMessageToWaBoxApp(String customId, String channelPhoneNumber, String contactPhoneNumber, String text);
}
