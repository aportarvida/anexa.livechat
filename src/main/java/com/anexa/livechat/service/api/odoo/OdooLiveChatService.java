package com.anexa.livechat.service.api.odoo;

import com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto;

public interface OdooLiveChatService {
	
	void sendMessageToOdoo(int channel, String db, String user, String pwd, WaBoxAppRequestDto request);
}
