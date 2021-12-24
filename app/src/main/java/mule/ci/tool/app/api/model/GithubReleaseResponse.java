package mule.ci.tool.app.api.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GithubReleaseResponse {

	private String id;
	
	private String node_id;
	
	private String tag_name;
	
	private String target_commitish;
	
	private Boolean draft;
	
	private Boolean prerelease;
	
	private List<Map<String,Object>> assets;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}

	public String getTarget_commitish() {
		return target_commitish;
	}

	public void setTarget_commitish(String target_commitish) {
		this.target_commitish = target_commitish;
	}

	public Boolean getDraft() {
		return draft;
	}

	public void setDraft(Boolean draft) {
		this.draft = draft;
	}

	public Boolean getPrerelease() {
		return prerelease;
	}

	public void setPrerelease(Boolean prerelease) {
		this.prerelease = prerelease;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public List<Map<String, Object>> getAssets() {
		return assets;
	}

	public void setAssets(List<Map<String, Object>> assets) {
		this.assets = assets;
	}
}
