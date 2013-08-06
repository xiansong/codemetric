package xian.visitor.model;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class CommitData {
	private List<UserClass> ucs;
	private Set<CallModel> cms;

	private int cyclo;
	private double volume;
	private double ratio;

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
			ratio += uc.getRatio();
		}
		return cyclo;
	}

	public double getVolumes() {
		if (volume != 0.0) {
			return volume;
		}
		for (UserClass uc : ucs) {
			cyclo += uc.getCyclomatic();
			volume += uc.getVolume();
			ratio += uc.getRatio();
		}
		return volume;
	}

	public double getRatio() {
		if (ratio != 0.0)
			return ratio;
		for (UserClass uc : ucs) {
			cyclo += uc.getCyclomatic();
			volume += uc.getVolume();
			ratio += uc.getRatio();
		}
		return ratio;
	}

	public double getInteraction() {
		double sum = 0.0;
		for (Iterator<CallModel> itr = cms.iterator(); itr.hasNext();) {
			sum += itr.next().getComplexity();
		}
		return sum;
	}

}
