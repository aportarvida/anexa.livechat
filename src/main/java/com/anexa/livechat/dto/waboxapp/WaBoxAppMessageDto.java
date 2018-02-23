package com.anexa.livechat.dto.waboxapp;

import java.io.Serializable;

import com.anexa.livechat.enums.WaBoxAppMessageDirectionType;
import com.anexa.livechat.enums.WaBoxAppMessageType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaBoxAppMessageDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String timestamp;

	private String uid;

	private String customUid;

	private WaBoxAppMessageDirectionType direction;

	private WaBoxAppMessageType type;

	private WaBoxAppBodyDto body;

	private int ack;
}
