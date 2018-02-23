package com.anexa.livechat.service.impl.odoo;

import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anexa.livechat.configuration.OdooProperties;
import com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto;
import com.anexa.livechat.service.api.mail.MailService;
import com.anexa.livechat.service.api.odoo.OdooLiveChatAlertService;
import com.anexa.livechat.service.api.odoo.OdooLiveChatService;
import com.anexa.livechat.service.api.odoo.OdooLiveChatSessionService;
import com.anexa.livechat.service.api.waboxapp.WaBoxAppService;

import emoji4j.EmojiUtils;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OdooLiveChatServiceImpl implements OdooLiveChatService {

	private static final String MODEL_MAIL_CHANNEL = "mail.channel";

	private static final String METHOD_MESSAGE_POST = "message_post";

	@Autowired
	private OdooProperties odooProperties;

	@Autowired
	private OdooLiveChatSessionService sessionService;

	@Autowired
	private WaBoxAppService waBoxAppService;

	@Autowired
	private OdooLiveChatAlertService alertService;

	@Autowired
	private MailService mailService;

	@Override
	public void sendMessageToOdoo(int channel, String db, String user, String pwd, WaBoxAppRequestDto request) {
		if (discard(request)) {
			return;
		}

		String channelPhoneNumber = request.getChannelPhoneNumber();
		String contactPhoneNumber = request.getContact().getPhoneNumber();

		log("createMessage(request)", request);
		val odooMessage = createOdooMessage(request);

		try {
			for (int i = 0; true; i++) {
				log("sessionService.getMailSession(channelPhoneNumber,contactPhoneNumber)", request);
				val session = sessionService.getMailSession(channel, db, user, pwd, channelPhoneNumber, contactPhoneNumber);

				try {
					log("sessionService.getUserId()", request);
					int uid = sessionService.getUserId(db, user, pwd);

					val args = asList(session.getResId());

					log("sessionService.execute(db, uid, pwd, MODEL_MAIL_CHANNEL, METHOD_MESSAGE_POST, args, odooMessage), session:" + session.getResId(), request);
					sessionService.execute(db, uid, pwd, MODEL_MAIL_CHANNEL, METHOD_MESSAGE_POST, args, odooMessage);
					break;
				} catch (SessionDeletedException e) {
					log("catch (" + e.getClass().getSimpleName() + " e), exception:" + e.getMessage(), request);

					if (i < odooProperties.getMaxAttemptCount()) {
						sessionService.deleteSession(channel, db, session.getChannelPhoneNumber(), session.getContactPhoneNumber());
					} else {
						throw e;
					}
				}
			}
		} catch (RuntimeException e) {
			log.error("catch (" + e.getClass().getSimpleName() + " e), exception:" + e.getMessage());
			e.printStackTrace();
			sendMessageToWaBoxApp(channelPhoneNumber, contactPhoneNumber, e);
			// alert(request, e);
		}
	}

	protected void sendMessageToWaBoxApp(String channelPhoneNumber, String contactPhoneNumber,
			RuntimeException exception) {
		String cuid = UUID.randomUUID().toString();
		String text;

		if (exception instanceof SessionConnectionRefusedException) {
			text = "No ha sido posible contactar a un operador disponible para atender su solicitud.";
		} else if (exception instanceof SessionDeletedException) {
			text = "No se pudo restablecer la conversación con el o con otro operador disponible.";
		} else {
			text = "En estos momentos estamos teniendo un problema técnicos con la plataforma WhatsApp.";
		}
		text += "Nuestro equipo de atención al cliente intentará contactarlo lo antes posible.";

		try {
			waBoxAppService.sendMessageToWaBoxApp(cuid, channelPhoneNumber, contactPhoneNumber,
					text + LocalDateTime.now());
		} catch (RuntimeException e) {
			log.error("catch (" + e.getClass().getSimpleName() + " e), exception:" + e.getMessage());
		}
	}

	protected void alert(WaBoxAppRequestDto request, RuntimeException exception) {
		try {
			val mailMessage = alertService.getMessage(request, exception);
			mailService.sendMail(mailMessage);
		} catch (RuntimeException e) {
			log.error("catch (" + e.getClass().getSimpleName() + " e), exception:" + e.getMessage());
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --- MESSAGE
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	protected boolean discard(WaBoxAppRequestDto request) {
		boolean result = false;

		switch (request.getMessage().getDirection()) {
		case i:
			break;
		default:
			return true;
		}

		switch (request.getEvent()) {
		case message:
			break;
		default:
			return true;
		}

		return result;
	}

	private Map<String, Object> createOdooMessage(WaBoxAppRequestDto request) {
		String body = request.getMessage().getBody().getText();
		String xPhoneNumber = request.getChannelPhoneNumber();
		String replyTo = request.getContact().getPhoneNumber();

		String text = EmojiUtils.shortCodify(body);
		
		val result = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("message_type", "comment");
				put("subtype", "mail.mt_comment");
				put("content_subtype", "html");
				put("body", text);
				put("description", text);
				put("reply_to", replyTo);
				put("email_from", replyTo);
				put("x_phone_number", xPhoneNumber);

				put("subject", false);
				put("needaction", false);
				put("starred", false);
				put("mail_server_id", false);
				put("no_auto_thread", false);
				put("author_avatar", false);
				put("parent_id", false);
				put("author_id", false);
				put("website_published", true);
			}
		};

		return result;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --- Log
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	protected void log(String step, WaBoxAppRequestDto request) {
		val format = "dir:{}, User:{}, Channel:{}, step:{}, text:{}";
		val text = request.getMessage().getBody().getText();
		val dir = request.getMessage().getDirection();
		val user = request.getContact().getPhoneNumber();
		val channel = request.getChannelPhoneNumber();

		log.debug(format, dir, user, channel, step, text);
	}
}
