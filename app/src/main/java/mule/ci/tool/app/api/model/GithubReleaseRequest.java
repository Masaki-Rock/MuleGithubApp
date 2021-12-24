package mule.ci.tool.app.api.model;

public class GithubReleaseRequest {

	private String tag_name;
	
	private String target_commitish;
	
	private Boolean draft;
	
	private Boolean prerelease;

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
	
	
}
