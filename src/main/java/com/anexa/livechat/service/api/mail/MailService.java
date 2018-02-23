package com.anexa.livechat.service.api.mail;

import com.anexa.livechat.dto.alert.MailMessageDto;

public interface MailService {

	void sendMail(MailMessageDto mailMessage);
}