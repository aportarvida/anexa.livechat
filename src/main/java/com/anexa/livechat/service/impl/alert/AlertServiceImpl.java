package com.anexa.livechat.service.impl.alert;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StrSubstitutor;
import org.springframework.core.io.ClassPathResource;

import com.anexa.livechat.service.api.alert.AlertService;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AlertServiceImpl<T, M> implements AlertService<T, M> {

	public static final String ALERT_SUBJECT = "ALERT_SUBJECT";

	public static final String ALERT_THROWABLE_SIMPLE_NAME = "ALERT_THROWABLE_SIMPLE_NAME";

	public static final String ALERT_THROWABLE_MESSAGE = "ALERT_THROWABLE_MESSAGE";

	public static final String ALERT_THROWABLE_STACK_TRACE = "ALERT_THROWABLE_STACK_TRACE";

	@Override
	public M getMessage(T request) {
		return getMessage(request, null);
	}

	// -------------------------------------------------------------------------------------
	// REPORT
	// -------------------------------------------------------------------------------------
	@Override
	final public M getMessage(T request, Throwable t) {
		val code = getCode();

		log.debug("Inicio operación generación de mensaje de alerta:{}", code);

		val subject = getSubject();

		val error = getError(t);

		val data = getData(request);
		data.put(ALERT_SUBJECT, subject);
		data.putAll(error);

		val content = getContent(code, data);

		val attachments = getAttachments(code, data);

		val result = createMessage(code, subject, content, attachments);

		log.debug("Mensaje de alerta generado");

		return result;
	}

	// -------------------------------------------------------------------------------------
	// CODE / SUBJECT / DATA
	// -------------------------------------------------------------------------------------
	abstract protected String getCode();

	abstract protected String getSubject();

	abstract protected Map<String, Object> getData(T request);

	// -------------------------------------------------------------------------------------
	// ERROR
	// -------------------------------------------------------------------------------------
	private Map<String, Object> getError(Throwable t) {
		val result = new HashMap<String, Object>();

		String simpleName;
		String message;
		String stackTrace;

		if (t != null) {
			val writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));

			simpleName = t.getClass().getSimpleName();
			message = t.getMessage();
			stackTrace = writer.toString();
		} else {
			message = "No disponible";
			simpleName = "No disponible";
			stackTrace = "No disponible";
		}

		result.put(ALERT_THROWABLE_SIMPLE_NAME, simpleName);
		result.put(ALERT_THROWABLE_MESSAGE, message);
		result.put(ALERT_THROWABLE_STACK_TRACE, stackTrace);

		return result;
	}

	// -------------------------------------------------------------------------------------
	// CONTENT
	// -------------------------------------------------------------------------------------
	protected String getContent(String code, Map<String, Object> valueMap) {
		String pathContent = getContentPathResource();
		String template = getTemplate(pathContent);

		val result = StrSubstitutor.replace(template, valueMap);
		return result;
	}

	protected abstract String getContentPathResource();

	protected String getTemplate(String path) {
		try {
			val result = new String(Files.readAllBytes((new ClassPathResource(path).getFile()).toPath()));
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Error al acceder al recurso:" + path, e);
		}
	}

	// -------------------------------------------------------------------------------------
	// ATTACHMENTS
	// -------------------------------------------------------------------------------------
	protected File[] getAttachments(String reportCode, Map<String, Object> data) {
		return new File[0];
	}

	// -------------------------------------------------------------------------------------
	// MESSAGE
	// -------------------------------------------------------------------------------------
	protected abstract M createMessage(String code, String subject, String content, File[] attachments);

	// -------------------------------------------------------------------------------------
	// FORMATTERS
	// -------------------------------------------------------------------------------------
	protected static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	protected static final DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	protected DateTimeFormatter getFormatterDate() {
		return formatterDate;
	}

	protected DateTimeFormatter getFormatterDateTime() {
		return formatterDateTime;
	}
}