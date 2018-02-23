package com.anexa.livechat.service.impl.odoo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.anexa.livechat.dto.alert.MailMessageDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto;
import com.anexa.livechat.service.api.odoo.OdooLiveChatAlertService;
import com.anexa.livechat.service.impl.alert.AlertServiceImpl;

import lombok.val;

@Service
public class OdooLiveChatAlertServiceImpl extends AlertServiceImpl<WaBoxAppRequestDto, MailMessageDto>
		implements OdooLiveChatAlertService {

	protected static final String ALERT_CODE = "ODOO_LIVE_CHAT";

	protected static final String CHANNEL_PHONE_NUMBER = "CHANNEL_PHONE_NUMBER";
	
	protected static final String CONTACT_PHONE_NUMBER = "CONTACT_PHONE_NUMBER";
	
	protected static final String MESSAGE_BODY_TEXT = "MESSAGE_BODY_TEXT";

	@Override
	protected String getCode() {
		return ALERT_CODE;
	}

	@Override
	protected String getSubject() {
		return "Alerta Odoo LiveChat - WhatsApp";
	}

	@Override
	protected Map<String, Object> getData(WaBoxAppRequestDto request) {
		val result = new HashMap<String, Object>();
		result.put(CHANNEL_PHONE_NUMBER, request.getChannelPhoneNumber());
		result.put(CONTACT_PHONE_NUMBER, request.getContact().getPhoneNumber());
		result.put(MESSAGE_BODY_TEXT, request.getMessage().getBody().getText());
		return result;
	}

	@Override
	protected String getContentPathResource() {
		return "templates\\alert.html";
	}

	@Override
	protected MailMessageDto createMessage(String code, String subject, String content, File[] attachments) {
		return new MailMessageDto(code, subject, content, attachments);
	}
}
