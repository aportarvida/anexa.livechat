package com.anexa.livechat.web.api.odoo;

import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.CHANNEL_PHONE_NUMBER_UID;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.CONTACT_NAME;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.CONTACT_PHONE_NUMBER_UID;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.CONTACT_TYPE;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.EVENT;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_ACK;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_IMAGE_CAPTION;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_IMAGE_MIME_TYPE;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_IMAGE_SIZE;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_IMAGE_THUMB;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_IMAGE_URL;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_BODY_TEXT;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_CUID;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_DIRECTION;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_DTM;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_TYPE;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.MESSAGE_UID;
import static com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto.TOKEN;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anexa.livechat.configuration.OdooProperties;
import com.anexa.livechat.dto.waboxapp.WaBoxAppBodyDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppContactDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppMessageDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto;
import com.anexa.livechat.enums.WaBoxAppContactType;
import com.anexa.livechat.enums.WaBoxAppEventType;
import com.anexa.livechat.enums.WaBoxAppMessageDirectionType;
import com.anexa.livechat.enums.WaBoxAppMessageType;
import com.anexa.livechat.service.api.odoo.OdooLiveChatService;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/odoo/livechat/{channel}/{db}")
@CrossOrigin
@Slf4j
public class OdooLiveChatController {

	@Autowired
	private OdooProperties odooProperties;

	@Autowired
	private OdooLiveChatService service;

	// @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
	// params = { "user", "pwd" })
	@PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<?> sendMessageToOdoo(@PathVariable int channel, @PathVariable String db,
			@RequestParam(required = false) String user, @RequestParam(required = false) String pwd,
			@RequestParam Map<String, String> params) {
		if (user == null || "".equals(user)) {
			user = odooProperties.getDefaultUser();
		}

		if (pwd == null || "".equals(pwd)) {
			pwd = odooProperties.getDefaultPassword();
		}

		try {
			log.info("step:{}, channel:{}, db:{}, params:{}", "val request = getRequest(params)", channel, db, params);

			val request = getRequest(params);
			try {
				log("service.sendMessageToOdoo(channel, db, user, pwd, request)", request);
				service.sendMessageToOdoo(channel, db, user, pwd, request);

				log("return ResponseEntity.ok()", request);
				return ResponseEntity.ok("");
			} catch (RuntimeException e) {
				log("catch (" + e.getClass().getSimpleName() + " e), exception:" + e.getMessage(), request);
				throw e;
			}
		} catch (RuntimeException e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	protected void log(String step, WaBoxAppRequestDto request) {
		val format = "dir:{}, User:{}, Channel:{}, step:{}, text:{}";
		val text = request.getMessage().getBody().getText();
		val dir = request.getMessage().getDirection();
		val user = request.getContact().getPhoneNumber();
		val channel = request.getChannelPhoneNumber();

		log.info(format, dir, user, channel, step, text);
	}

	protected WaBoxAppRequestDto getRequest(Map<String, String> params) {
		// @formatter:off
		val result = WaBoxAppRequestDto
				.builder()
				.event(getEnumParam(params, EVENT, WaBoxAppEventType.class))
				.token(getStringParam(params, TOKEN))
				.channelPhoneNumber(getStringParam(params, CHANNEL_PHONE_NUMBER_UID))
				.contact(getContact(params))
				.message(getMessage(params))
				.build(); 
		// @formatter:on

		return result;
	}

	protected WaBoxAppContactDto getContact(Map<String, String> map) {
		// @formatter:off
		val result = WaBoxAppContactDto
				.builder()
				.type(getEnumParam(map, CONTACT_TYPE, WaBoxAppContactType.class))
				.phoneNumber(getStringParam(map, CONTACT_PHONE_NUMBER_UID))
				.name(getStringParam(map, CONTACT_NAME))
				.build();
 		// @formatter:on

		return result;
	}

	protected WaBoxAppMessageDto getMessage(Map<String, String> map) {
		// @formatter:off
		val result = WaBoxAppMessageDto
				.builder()
				.timestamp(getStringParam(map, MESSAGE_DTM))
				.uid(getStringParam(map, MESSAGE_UID))
				.customUid(getStringParam(map, MESSAGE_CUID))
				.direction(getEnumParam(map, MESSAGE_DIRECTION, WaBoxAppMessageDirectionType.class))
				.type(getEnumParam(map, MESSAGE_TYPE, WaBoxAppMessageType.class))
				.body(getBody(map))
				.ack(getIntegerParam(map, MESSAGE_ACK))
				.build();
 		// @formatter:on

		return result;
	}

	private WaBoxAppBodyDto getBody(Map<String, String> map) {
		// @formatter:off
		val result = WaBoxAppBodyDto
				.builder()
				.text(getStringParam(map, MESSAGE_BODY_TEXT))
				.caption(getStringParam(map, MESSAGE_BODY_IMAGE_CAPTION))
				.mimetype(getStringParam(map, MESSAGE_BODY_IMAGE_MIME_TYPE))
				.url(getStringParam(map, MESSAGE_BODY_IMAGE_URL))
				.thumb(getStringParam(map, MESSAGE_BODY_IMAGE_THUMB))
				.size(getLongParam(map, MESSAGE_BODY_IMAGE_SIZE))
				.build();
 		// @formatter:on

		return result;
	}

	protected static String getStringParam(Map<String, String> map, String key) {
		String result = map.get(key);
		if (result == null) {
			result = "";
		}
		return result;
	}

	protected static <E extends Enum<E>> E getEnumParam(Map<String, String> map, String key, Class<E> enumeration) {
		val value = getStringParam(map, key);
		try {
			E result = Enum.valueOf(enumeration, value);
			return result;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid value for enum " + enumeration.getSimpleName() + ": " + value);
		}
	}

	protected static Long getLongParam(Map<String, String> map, String key) {
		val value = getStringParam(map, key);
		try {
			Long result = null;
			if (!"".equals(value)) {
				result = Long.parseLong(value);
			}
			return result;
		} catch (

		NumberFormatException e) {
			throw new RuntimeException("Invalid format for long value " + value);
		}
	}

	protected static Integer getIntegerParam(Map<String, String> map, String key) {
		val value = getStringParam(map, key);
		try {
			Integer result = null;
			if (!"".equals(value)) {
				result = Integer.parseInt(value);
			}
			return result;
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid format for inetger value " + value);
		}
	}
}
