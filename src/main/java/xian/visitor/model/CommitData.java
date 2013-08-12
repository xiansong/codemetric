package xian.visitor.model;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class CommitData {
	private final List<UserClass> ucs;
	private final Set<CallModel> cms;

	public CommitData(final List<UserClass> ucs, final Set<CallModel> cms) {
		this.ucs = ucs;
		this.cms = cms;
	}

	public Set<CallModel> getCms() {
		return cms;
	}

	public List<UserClass> getUcs() {
		return ucs;
	}

	public int getCyclomatics() {
		int cyclo = 0;
		for (UserClass uc : ucs) {
			cyclo += uc.getCyclomatic();
		}
		return cyclo;
	}

	public double getVolumes() {
		double volume =0.0;
		for (UserClass uc : ucs) {
			volume += uc.getVolume();
		}
		return volume;
	}

	public double getRatio() {
		double ratio = 0.0;
		for (UserClass uc : ucs) {
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
