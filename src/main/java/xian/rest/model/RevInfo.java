package xian.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

@XmlRootElement
public final class RevInfo {

	private double[] cyclo;
	private double[] volume;
	private double[] calls;
	private double cycloVar;
	private double volVar;
	private double callVar;

	public RevInfo(final double[] c, final double[] v, final double[] cal) {
		cyclo = c;
		volume = v;
		calls = cal;

		StandardDeviation sd = new StandardDeviation(true);
		cycloVar = sd.evaluate(cyclo) / StatUtils.mean(cyclo);
		volVar = sd.evaluate(volume) / StatUtils.mean(volume);
		callVar = sd.evaluate(calls) / StatUtils.mean(calls);
	}

	@XmlElement
	public double[] getCyclo() {
		return cyclo;
	}

	@XmlElement
	public double[] getVolume() {
		return volume;
	}

	@XmlElement
	public double[] getCalls() {
		return calls;
	}

	@XmlElement
	public double getCycloVar() {
		return cycloVar;
	}

	@XmlElement
	public double getVolVar() {
		return volVar;
	}

	@XmlElement
	public double getCallVar() {
		return callVar;
	}

}
