package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppSendResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private boolean success;
	
	@JsonProperty("custom_uid")
	private String customUid;
}
