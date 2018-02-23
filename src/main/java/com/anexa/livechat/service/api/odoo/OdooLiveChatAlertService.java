package com.anexa.livechat.service.api.odoo;

import com.anexa.livechat.dto.alert.MailMessageDto;
import com.anexa.livechat.dto.waboxapp.WaBoxAppRequestDto;
import com.anexa.livechat.service.api.alert.AlertService;

public interface OdooLiveChatAlertService extends AlertService<WaBoxAppRequestDto, MailMessageDto> {

}
