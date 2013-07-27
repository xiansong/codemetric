package xian.model;

import java.util.List;

public class CommitData {
	private List<UserClass> ucs;
	private List<CallModel> cms;

	public List<CallModel> getCms() {
		return cms;
	}

	public void setCms(final List<CallModel> cms) {
		this.cms = cms;
	}

	public List<UserClass> getUcs() {
		return ucs;
	}

	public void setUcs(final List<UserClass> ucs) {
		this.ucs = ucs;
	}

}
