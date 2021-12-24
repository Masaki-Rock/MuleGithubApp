package mule.ci.tool.app.util;

import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Const {
		
	private static final Logger log = LoggerFactory.getLogger(Const.class);
		
	public static final String ASSETS_UPLOAD_END_POINT = "https://uploads.github.com/repos/%s/%s/releases/%s/assets?name=%s";

	public static final String ASSETS_DOWNLOAD_END_POINT = "https://github.com/%s/%s/releases/download/%s/%s";

	public static final String RELEASES_END_POINT = "https://api.github.com/repos/%s/%s/releases%s%s";

	public static final String GET = "GET";

	public static final String POST = "POST";

	public static final String PUT = "PUT";

	public static final String PATCH = "PATCH";

	public static final String DELETE = "DELETE";
	
	public static String ACCOUNT_ID;

	public static String REPOSITORY_ID;

	public static String ACCESS_TOKEN;

	public static String RELEASE_NAME;

	public static Boolean PRERELEASE_FLAG = true;

	public static String BRANCH = "main";
	
	public static String PROJECT_HOME = "";
	
	public static String APPLICATION_FILE_NAME = "";
	
	public static String APPLICATION_FILE_PATH = "";
	
	public static String ARTIFACT_NAME = "";
	
	public static void init() throws AppException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new AppException(e);
		}
		Document document;
		if (StringUtils.isNotBlank(PROJECT_HOME)) {
			PROJECT_HOME += "/";
		}
		String pomFilePath = PROJECT_HOME + "pom.xml";
		try {
			document = builder.parse(Paths.get(pomFilePath).toFile());
		} catch (SAXException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
		
		Element project = document.getDocumentElement();
		Node artifactId = project.getElementsByTagName("artifactId").item(0);
		Node version = project.getElementsByTagName("version").item(0);
		Node packaging = project.getElementsByTagName("packaging").item(0);
		if (artifactId == null && version == null && packaging == null) throw new AppException("Settings is not enough.");
		String artifaceName = artifactId.getTextContent() + "-" + version.getTextContent() + "-" + packaging.getTextContent() + ".jar";
		APPLICATION_FILE_PATH = PROJECT_HOME + "target/" + artifaceName;
		APPLICATION_FILE_NAME = artifactId.getTextContent() +"-" + version.getTextContent() + "-%s.jar";
		log.debug("applicationFileName : {}", APPLICATION_FILE_NAME);
		
		Element properties = (Element) project.getElementsByTagName("properties").item(0);
		Node accountId = properties.getElementsByTagName("accountId").item(0);
		if (accountId != null) ACCOUNT_ID = accountId.getTextContent();		
		log.debug("accountId : {}", ACCOUNT_ID);
		Node repositoryId = properties.getElementsByTagName("repositoryId").item(0);
		if (repositoryId != null) REPOSITORY_ID = repositoryId.getTextContent();
		log.debug("repositoryId : {}", REPOSITORY_ID);
		Node token = properties.getElementsByTagName("token").item(0);
		if (token != null) ACCESS_TOKEN = token.getTextContent();
		log.debug("token : {}", ACCESS_TOKEN);
		Node preReleaseFlag = properties.getElementsByTagName("preReleaseFlag").item(0);
		if (preReleaseFlag != null) PRERELEASE_FLAG = Boolean.getBoolean(preReleaseFlag.getTextContent());
		log.debug("preReleaseFlag : {}", PRERELEASE_FLAG);
		Node branch = properties.getElementsByTagName("branch").item(0);
		if (branch != null) BRANCH = branch.getTextContent();
		log.debug("branch : {}", BRANCH);
		Node releaseName = properties.getElementsByTagName("releaseName").item(0);
		if (releaseName != null) RELEASE_NAME = releaseName.getTextContent() + "-" + BRANCH;
		log.debug("releaseName : {}", RELEASE_NAME);
	}
}
