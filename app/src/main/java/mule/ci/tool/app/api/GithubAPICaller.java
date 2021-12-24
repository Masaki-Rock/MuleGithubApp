package mule.ci.tool.app.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mule.ci.tool.app.api.model.GithubReleaseRequest;
import mule.ci.tool.app.api.model.GithubReleaseResponse;
import mule.ci.tool.app.util.AppException;
import mule.ci.tool.app.util.Const;
import mule.ci.tool.app.util.HttpClientUtil;

public class GithubAPICaller {

	private static final Logger log = LoggerFactory.getLogger(GithubAPICaller.class);

	/**
	 * リリース�?報検索機�?�
	 * 
	 * @return 検索結果
	 * @throws AppException アプリケーション例�?
	 */
	public GithubReleaseResponse[] findRelease() throws AppException {

		String path = String.format(Const.RELEASES_END_POINT, Const.ACCOUNT_ID, Const.REPOSITORY_ID, StringUtils.EMPTY, StringUtils.EMPTY);
		String resbody = HttpClientUtil.sendRequest(path, Const.GET, null);

		GithubReleaseResponse[] res = HttpClientUtil.makeResponse(resbody, GithubReleaseResponse[].class);
		log.debug("findReleases. {}", HttpClientUtil.toJson(res));
		return res;
	}
	
	/**
	 * リリース�?報検索機�?�
	 * 
	 * @param releaseName リリースフォル�?�?
	 * @return 検索結果
	 * @throws AppException アプリケーション例�?
	 */
	public GithubReleaseResponse getRelease(String releaseName) throws AppException {

		GithubReleaseResponse[] releases = findRelease();
		
		for (GithubReleaseResponse release: releases) {
			if (StringUtils.equals(releaseName, release.getTag_name())) {
				return release;
			}
		}
		return null;
	}
	
	/**
	 * リリースフォル�?登録機�?�
	 * 
	 * @param tagName タブ名
	 * @return 検索結果
	 * @throws AppException アプリケーション例�?
	 */
	public GithubReleaseResponse saveRelease(String tagName, Boolean preRelease, String target) throws AppException {

		GithubReleaseRequest param = new GithubReleaseRequest();
		param.setTag_name(tagName);
		param.setDraft(false);
		param.setPrerelease(!preRelease);
		param.setTarget_commitish(target);
		String path = String.format(Const.RELEASES_END_POINT, Const.ACCOUNT_ID, Const.REPOSITORY_ID, StringUtils.EMPTY, StringUtils.EMPTY);
		String resbody = HttpClientUtil.sendRequest(path, Const.POST, param);

		GithubReleaseResponse res = HttpClientUtil.makeResponse(resbody, GithubReleaseResponse.class);
		log.debug("saveRelease. {}", HttpClientUtil.toJson(res));
		return res;
	}
	
	/**
	 * アセ�?ト情報検索機�?�
	 * 
	 * @param releaseName リリースフォル�?�?
	 * @return 検索結果
	 * @throws AppException アプリケーション例�?
	 */
	public List<Map<String, Object>> findAsset(String releaseName) throws AppException {

		GithubReleaseResponse release = getRelease(releaseName);
		log.debug("Find release tag name is {}. and get {}.", releaseName, release != null);
		if (release == null) {
			throw new AppException("The release record could not be acquired.");
		}
		return release.getAssets();
	}
	/**
	 * リリース�?報検索機�?�
	 * 
	 * @param releaseId リリースフォル�?ID
	 * @param filepath ファイル格納パス
	 * @param fileName ア�?プロードファイル�?
	 * @return 登録結果
	 * @throws AppException アプリケーション例�?
	 */
	public Boolean saveAssets(String releaseId, String filepath, String assetName) throws AppException {

		File upfile = new File(filepath);
		if (!upfile.exists()) {
			throw new AppException("A Upload file is not found.");
		}
		
		String urlParam = String.format(Const.ASSETS_UPLOAD_END_POINT, Const.ACCOUNT_ID, Const.REPOSITORY_ID, releaseId, assetName);
		String resbody = HttpClientUtil.sendRequestWithFile(urlParam, Const.POST, upfile);

		GithubReleaseResponse res = HttpClientUtil.makeResponse(resbody, GithubReleaseResponse.class);
		if (StringUtils.isBlank(res.getId())) {
			throw new AppException("The uploading could not be finished.");
		}
		return true;
	}
	
	/**
	 * アプリケーションファイルダウンロード
	 * 
	 * @param tagName タブ名
	 * @param filepath ファイル保存�?�（ファイル名含�??�?
	 * @param assetName ファイル�?
	 * @return 検索結果
	 * @throws AppException アプリケーション例�?
	 */
	public Boolean getAssets(String tagName, String filepath, String assetName) throws AppException {

		String urlParam = String.format(Const.ASSETS_DOWNLOAD_END_POINT, Const.ACCOUNT_ID, Const.REPOSITORY_ID, tagName, assetName);
		byte[] filedata = HttpClientUtil.sendRequestForDownload(urlParam, Const.GET);
		try {
			Files.write(Paths.get(filepath), filedata);
		} catch (IOException e) {
			throw new AppException(e);
		}
		log.debug("file down load finished.");
		return true;
	}
}
