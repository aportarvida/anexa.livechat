package com.anexa.livechat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.anexa.livechat.enums.WaBoxAppContactType;
import com.anexa.livechat.enums.WaBoxAppEventType;
import com.anexa.livechat.enums.WaBoxAppMessageDirectionType;
import com.anexa.livechat.enums.WaBoxAppMessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppMessage {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private int resId;

    private WaBoxAppEventType event;
    
    private String channelPhoneNumber;
    
    private String contactPhoneNumber;
    
	private String name;

	private WaBoxAppContactType contactType;
    
	private WaBoxAppMessageDirectionType direction;

	private WaBoxAppMessageType messageType;

	private String text;
}
