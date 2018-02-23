package com.anexa.livechat.service.impl.odoo;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.anexa.livechat.configuration.OdooProperties;
import com.anexa.livechat.domain.Session;
import com.anexa.livechat.repository.SessionRepository;
import com.anexa.livechat.service.api.odoo.OdooLiveChatSessionService;

import lombok.val;

@Service
public class OdooLiveChatSessionServiceImpl implements OdooLiveChatSessionService {

	private static final String MODEL_IM_LIVECHAT_CHANNEL = "im_livechat.channel";

	private static final String METHOD_GET_MAIL_CHANNEL = "get_mail_channel";

	private static final String EXCEPTION_MESSAGE_SESSION_DOES_NOT_EXIST = "(u'Record does not exist or has been deleted.', None)";

	@Autowired
	private OdooProperties odooProperties;

	@Autowired
	private SessionRepository repository;

	@Override
	@Cacheable(cacheNames = "session", key = "{#channel, #db, #channelPhoneNumber, #contactPhoneNumber}")
	public Session getMailSession(int channel, String db, String user, String pwd, String channelPhoneNumber,
			String contactPhoneNumber) {

		Session result = repository.findOneByChannelAndDbAndChannelPhoneNumberAndContactPhoneNumber(channel, db, channelPhoneNumber, contactPhoneNumber);

		if (result == null) {
			int resId = getNewMailSession(channel, db, user, pwd, contactPhoneNumber);

			// @formatter:off
			result = Session
					.builder()
					.channel(channel)
					.db(db)
					.channelPhoneNumber(channelPhoneNumber)
					.contactPhoneNumber(contactPhoneNumber)
					.resId(resId)
					.build();
			// @formatter:on
			result = repository.saveAndFlush(result);
		}

		return result;
	}

	public int getNewMailSession(int channel, String db, String user, String pwd, String anonymousName) {
		int uid = getUserId(db, user, pwd);
		val model = MODEL_IM_LIVECHAT_CHANNEL;
		val method = METHOD_GET_MAIL_CHANNEL;
		val args = asList(channel, anonymousName);
		val kwargs = new HashMap<String, Object>();

		val response = execute(db, uid, pwd, model, method, args, kwargs);

		if (response instanceof Map) {
			@SuppressWarnings("unchecked")
			val result = (Integer) ((Map<String, Object>) response).get("id");

			return result;
		} else {
			throw new SessionConnectionRefusedException();
		}
	}

	@Override
	@CacheEvict(cacheNames = "session", key = "{#channel, #db, #channelPhoneNumber, #contactPhoneNumber}")
	public void deleteSession(int channel, String db, String channelPhoneNumber, String contactPhoneNumber) {
		val session = repository.findOneByChannelAndDbAndChannelPhoneNumberAndContactPhoneNumber(channel, db,
				channelPhoneNumber, contactPhoneNumber);

		if (session != null) {
			repository.delete(session.getId());
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --- User ID
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	@Cacheable(cacheNames = "user", key = "{#db, #user, #pwd}")
	public int getUserId(String db, String user, String pwd) {
		String url = odooProperties.getUrl();

		try {
			val client = getUnauthenticatedClient(url);
			val response = (int) client.execute("authenticate", asList(db, user, pwd, emptyMap()));
			return response;
		} catch (MalformedURLException | XmlRpcException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --- XML RPC
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	public Object execute(String db, int uid, String pwd, String model, String method, List<?> args,
			Map<String, Object> kwargs) {
		val url = odooProperties.getUrl();

		try {
			val client = getAuthenticatedClient(url);
			val response = client.execute("execute_kw", asList(db, uid, pwd, model, method, args, kwargs));
			return response;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (XmlRpcException e) {
			if (EXCEPTION_MESSAGE_SESSION_DOES_NOT_EXIST.equals(e.getMessage())) {
				throw new SessionDeletedException(e);
			} else {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	protected XmlRpcClient getAuthenticatedClient(final String url) throws MalformedURLException {
		val result = new XmlRpcClient() {
			{
				setConfig(new XmlRpcClientConfigImpl() {

					private static final long serialVersionUID = 1L;

					{
						setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
					}
				});
			}
		};

		return result;
	}

	protected XmlRpcClient getUnauthenticatedClient(final String url) throws MalformedURLException {
		val result = new XmlRpcClient() {
			{
				setConfig(new XmlRpcClientConfigImpl() {

					private static final long serialVersionUID = 1L;

					{
						setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));
					}
				});
			}
		};

		return result;
	}
}
