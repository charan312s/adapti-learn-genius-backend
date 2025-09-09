package com.adaptilearn.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIService {

	@Value("${GEMINI_API_KEY:AIzaSyDBqydOqitOXauNn6rfgnojOryYm_2s8hM}")
	private String geminiApiKey;

	private final RestTemplate rest = new RestTemplate();

	/**
	 * Return a short hint for the provided prompt. If GEMINI_API_KEY is not set
	 * or the API call fails, returns a human-friendly error message.
	 */
	public String getHint(String prompt) {
		if (geminiApiKey == null || geminiApiKey.isBlank()) {
			return "AI hint not available: GEMINI_API_KEY is not configured on the server.";
		}

		try {
			// Use the example curl from Google: call the generateContent endpoint and send the API key in X-goog-api-key header.
			String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("X-goog-api-key", geminiApiKey);

			// Build body: { "contents": [ { "parts": [ { "text": "..." } ] } ] }
			Map<String, Object> part = new HashMap<>();
			part.put("text", "Provide a concise hint (not the full solution) and a one-line suggestion for next steps for this question:\n" + prompt);

			Map<String, Object> content = new HashMap<>();
			content.put("parts", List.of(part));

			Map<String, Object> body = new HashMap<>();
			body.put("contents", List.of(content));

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
			ResponseEntity<Map<String, Object>> resp = rest.exchange(url, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {});

            log.info("The response from Gemini API:");
			if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                System.out.println("Inside if 2xx");
				String out = extractTextFromResponse(resp.getBody());
				if (out != null) return out;
			}

			return "No hint available from AI.";
		} catch (Exception e) {
			return "AI service error: " + e.getMessage();
		}
	}

	/**
	 * Extract text from several possible Gemini response shapes.
	 */
	private String extractTextFromResponse(Map<String, Object> resp) {
		if (resp == null) return null;

		// 1) 'candidates' -> [{ content: { parts: [ { text: '...' } ], ... }, ... } ]
		Object candidates = resp.get("candidates");
		if (candidates instanceof List) {
			List<?> candList = (List<?>) candidates;
			for (Object candObj : candList) {
				if (!(candObj instanceof Map)) continue;
				Map<?,?> cand = (Map<?,?>) candObj;
				Object content = cand.get("content");
				if (content instanceof Map) {
					Map<?,?> contentMap = (Map<?,?>) content;
					// Primary: content.parts[].text (the format you provided)
					Object partsObj = contentMap.get("parts");
					if (partsObj instanceof List) {
						List<?> parts = (List<?>) partsObj;
						for (Object partObj : parts) {
							if (!(partObj instanceof Map)) continue;
							Object text = ((Map<?,?>) partObj).get("text");
							if (text != null) return text.toString().trim();
						}
					}

					// Fallbacks inside content: direct 'text' or 'output'
					Object textDirect = contentMap.get("text");
					if (textDirect != null) return textDirect.toString().trim();

					Object output = contentMap.get("output");
					if (output instanceof Map) {
						Object t = ((Map<?,?>) output).get("text");
						if (t != null) return t.toString().trim();
					}
				}
			}
		}

		// 2) 'message' -> { 'content': [ { 'type': 'output_text', 'text': '...' } ] }
		Object messageObj = resp.get("message");
		if (messageObj instanceof Map) {
			Object contentObj = ((Map<?,?>) messageObj).get("content");
			if (contentObj instanceof List) {
				List<?> contentList = (List<?>) contentObj;
				for (Object item : contentList) {
					if (item instanceof Map) {
						Object text = ((Map<?,?>) item).get("text");
						if (text != null) return text.toString().trim();
					}
				}
			}
		}

		// 3) 'output' -> { 'text': '...' }
		Object output = resp.get("output");
		if (output instanceof Map) {
			Object text = ((Map<?,?>) output).get("text");
			if (text != null) return text.toString().trim();
		}

		// 3b) 'outputs' -> [ { 'content': [ { 'type':'output_text', 'text': '...' } ] } ] (generateContent)
		Object outputsObj = resp.get("outputs");
		if (outputsObj instanceof List) {
			List<?> outputs = (List<?>) outputsObj;
			if (!outputs.isEmpty() && outputs.get(0) instanceof Map) {
				Object contentObj = ((Map<?,?>) outputs.get(0)).get("content");
				if (contentObj instanceof List) {
					List<?> contentList = (List<?>) contentObj;
					for (Object item : contentList) {
						if (item instanceof Map) {
							Object text = ((Map<?,?>) item).get("text");
							if (text != null) return text.toString().trim();
						}
					}
				}
			}
		}

		// 4) top-level 'text'
		Object textTop = resp.get("text");
		if (textTop instanceof String) return ((String) textTop).trim();

		// 5) some responses may use 'candidates' -> [{ 'output': '...' }]
		if (candidates instanceof List) {
			List<?> candList = (List<?>) candidates;
			if (!candList.isEmpty() && candList.get(0) instanceof Map) {
				Object out = ((Map<?,?>) candList.get(0)).get("output");
				if (out instanceof String) return ((String) out).trim();
				if (out instanceof Map) {
					Object t = ((Map<?,?>) out).get("text");
					if (t != null) return t.toString().trim();
				}
			}
		}

		return null;
	}
}
