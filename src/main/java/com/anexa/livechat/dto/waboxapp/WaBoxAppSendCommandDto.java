package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppSendCommandDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String token;
	
	@JsonProperty("uid")
	private String contactPhoneNumber;

	@JsonProperty("to")
	private String agentPhoneNumber;

	@JsonProperty("custom_uid")
	private String customUid;

	private String text;
}
