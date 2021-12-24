package mule.ci.tool.app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class HttpClientUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * GitHub用RESTAPI送信処理
	 * 	
	 * @param urlParam URLパス
	 * @param method   HTTPメソッド
	 * @param body     リクエストボディー
	 * @return レスポンスボディー
	 * @throws AppException アプリケーション例外
	 */
	public static String sendRequest(String urlParam, String method, Object body) throws AppException {

		return sendRequestForJson(urlParam, method, body, makeAccessTokenHeader());
	}

	/**
	 * RESTAPI送信処理
	 * 
	 * @param urlParam URLパス
	 * @param method   HTTPメソッド
	 * @param body     リクエストボディー
	 * @param headers  ヘッダー
	 * @return レスポンスボディー
	 * @throws AppException アプリケーション例外
	 */
	public static String sendRequestForJson(String urlParam, String method, Object body, Map<String, String> headers)
			throws AppException {

		return sendRequestForJson(urlParam, method, toJson(body), headers);
	}

	/**
	 * RESTAPI送信処理
	 * 
	 * @param urlParam URLパス
	 * @param method   HTTPメソッド
	 * @param body     リクエストボディー
	 * @param headers  ヘッダー
	 * @return レスポンスボディー
	 * @throws AppException アプリケーション例外
	 */
	public static String sendRequestForJson(String urlParam, String method, String body, Map<String, String> headers)
			throws AppException {
		StringEntity entity = null;
		if (body != null) {
			entity = new StringEntity(body, "UTF-8");
			entity.setContentType("application/json");
		}
		log.debug("target path. {}　: {}", method, urlParam);
		log.debug("headers. {}", headers);
		log.debug("request body. {}", format(body));
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000)
				.setConnectionRequestTimeout(10000).build();
		HttpUriRequest httpUriRequest = RequestBuilder.create(method).setConfig(requestConfig)
				.setHeader("Content-Type", "application/json").setEntity(entity).setUri(urlParam).build();
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				httpUriRequest.setHeader(header.getKey(), header.getValue());
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpUriRequest);
		} catch (ClientProtocolException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
		HttpEntity responseEntity = response.getEntity();
		String res = null;
		if (responseEntity != null) {
			try {
				res = EntityUtils.toString(responseEntity);
				log.debug("response body. {}", format(res));
				return res;
			} catch (ParseException e) {
				throw new AppException(e);
			} catch (IOException e) {
				throw new AppException(e);
			}
		}
		res = response.getStatusLine().toString().trim().split(" ")[1];
		log.debug("statusLine. {}", res);
		return res;
	}

	/**
	 * バイナリファイルリクエスト送信処理
	 * 
	 * @param urlParam URLパス
	 * @param method   HTTPメソッド
	 * @param file     アップロードファイルデータ
	 * @return レスポンスボディー
	 * @throws AppException アプリケーション例外
	 */
	public static String sendRequestWithFile(String urlParam, String method, File file) throws AppException {
		
		Map<String, String> headers = makeAccessTokenHeader();
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(120000)
				.setConnectionRequestTimeout(120000).build();
		
	    log.debug("target path. {}　: {}", method, urlParam);
		log.debug("headers. {}", headers);
		log.debug("upload fine name is {}", file.getName());
	    FileEntity entity = new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);
		HttpUriRequest httpUriRequest = RequestBuilder.create(method).setConfig(requestConfig)
				.setHeader("Content-Type", "application/zip").setEntity(entity)
				.setUri(urlParam).build();
		if (headers != null) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				httpUriRequest.setHeader(header.getKey(), header.getValue());
			}
		}
	    
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpUriRequest);
		} catch (ClientProtocolException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
		HttpEntity responseEntity = response.getEntity();
		String res = null;
		if (responseEntity != null) {
			try {
				res = EntityUtils.toString(responseEntity);
				log.debug("response body. {}", format(res));
				return res;
			} catch (ParseException e) {
				throw new AppException(e);
			} catch (IOException e) {
				throw new AppException(e);
			}
		}
		res = response.getStatusLine().toString().trim().split(" ")[1];
		log.debug("statusLine. {}", res);
		return res;
	}
	
	/**
	 * ファイルダウンロード処理
	 * 
	 * @param urlParam URLパス
	 * @param method   HTTPメソッド
	 * @param headers  ヘッダー
	 * @return バイナリーファイルデータ
	 * @throws AppException アプリケーション例外
	 */
	public static byte[] sendRequestForDownload(String urlParam, String method) throws AppException {
		
//		Map<String, String> headers = makeAccessTokenHeaderforGithub();
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(120000)
				.setConnectionRequestTimeout(120000).build();
		
	    log.debug("target path. {}　: {}", method, urlParam);
	    
		HttpUriRequest httpUriRequest = RequestBuilder.create(method).setConfig(requestConfig)
				.setUri(urlParam).build();
	    
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpUriRequest);
		} catch (ClientProtocolException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity == null) {
			return new byte[0];
		}
		try {
			log.debug("success file download.");
			return EntityUtils.toByteArray(responseEntity);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
	
	public static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * アクセストークンヘッダー(GitHub)
	 * 
	 * @return ヘッダー情報
	 * @throws AppException アプリケーション例外
	 */
	public static Map<String, String> makeAccessTokenHeader() throws AppException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + Const.ACCESS_TOKEN);
		return headers;
	}

	public static <T> T makeResponse(String content, Class<T> valueType) throws AppException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			return mapper.readValue(content, valueType);
		} catch (JsonMappingException e) {
			throw new AppException(e);
		} catch (JsonProcessingException e) {
			throw new AppException(e);
		}
	}

	/**
	 * インデント形式に変換する
	 * 
	 * @param msg JSON文字列
	 * @return 整形後の文字列
	 */
	private static String format(String msg) {
		if (msg == null)
			return StringUtils.EMPTY;
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		JsonElement el = parser.parse(msg);
		return gson.toJson(el);
	}

	/**
	 * JSON変換機能
	 * 
	 * @param body オブジェクト
	 * @return JSONデータ
	 * @throws AppException アプリケーション例外
	 */
	public static String toJson(Object body) throws AppException {
		String request = null;
		if (body != null) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			try {
				request = mapper.writeValueAsString(body);
			} catch (JsonProcessingException e) {
				throw new AppException(e);
			}
		}
		return request;
	}
}
