package xian.model;

import java.util.List;
import java.util.Set;

public final class CommitData {
	private List<UserClass> ucs;
	private Set<CallModel> cms;

	public Set<CallModel> getCms() {
		return cms;
	}

	public void setCms(final Set<CallModel> cms) {
		this.cms = cms;
	}

	public List<UserClass> getUcs() {
		return ucs;
	}

	public void setUcs(final List<UserClass> ucs) {
		this.ucs = ucs;
	}

}
