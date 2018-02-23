package com.anexa.livechat.service.impl.waboxapp;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anexa.livechat.configuration.WaBoxAppProperties;
import com.anexa.livechat.dto.waboxapp.WaBoxAppSendCommandDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppSendResponseDto;
import com.anexa.livechat.service.api.waboxapp.WaBoxAppService;

import lombok.val;

@Service
public class WaBoxAppServiceImpl implements WaBoxAppService {

	@Autowired
	private WaBoxAppProperties waBoxAppProperties;

	@Override
	public boolean sendMessageToWaBoxApp(String customId, String agentPhoneNumber, String contactPhoneNumber,
			String text) {
		val uuid = customId + "-" + UUID.randomUUID().toString();
		// @formatter:off
		val command = WaBoxAppSendCommandDto
				.builder()
				.token(waBoxAppProperties.getToken())
				.customUid(uuid)
				.agentPhoneNumber(agentPhoneNumber)
				.contactPhoneNumber(contactPhoneNumber)
				.text(text)
				.build();
		// @formatter:on

		RestTemplate restTemplate = new RestTemplate();
		val request = new HttpEntity<>(command);
		val response = restTemplate.exchange(waBoxAppProperties.getUrl(), HttpMethod.POST, request, WaBoxAppSendResponseDto.class);

		val result = response.getBody();

		return result.isSuccess();

	}
}
