package xian.visitor.model;

import java.util.List;
import java.util.Set;

public final class CommitData {
	private List<UserClass> ucs;
	private Set<CallModel> cms;

	private int cyclo;
	private double volume;

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

	public int getCyclomatics() {
		if (cyclo != 0)
			return cyclo;
		for (UserClass uc : ucs) {
			cyclo += uc.getCyclomatic();
			volume += uc.getVolume();
		}
		return cyclo;
	}

	public double getVolumes() {
		if (volume != 0.0d) {
			return volume;
		}
		for (UserClass uc : ucs) {
			cyclo += uc.getCyclomatic();
			volume += uc.getVolume();
		}
		return volume;
	}

}
