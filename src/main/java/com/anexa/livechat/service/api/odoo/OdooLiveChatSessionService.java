package com.anexa.livechat.service.api.odoo;

import java.util.List;
import java.util.Map;

import com.anexa.livechat.domain.Session;

public interface OdooLiveChatSessionService {

	Session getMailSession(int channel, String db, String user, String pwd, String channelPhoneNumber,
			String contactPhoneNumber);

	void deleteSession(int channel, String db, String channelPhoneNumber, String contactPhoneNumber);

	public int getUserId(String db, String user, String pwd);

	Object execute(String db, int uid, String pwd, String model, String method, List<?> args, Map<String, Object> kwargs);
}
