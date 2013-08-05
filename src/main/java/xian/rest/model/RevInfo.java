package xian.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

@XmlRootElement
public final class RevInfo {

	private final double[] cyclo;
	private final double[] volume;
	private final double[] call;
	private final double[] ratio;
	private final double cycloVar;
	private final double volumeVar;
	private final double callVar;
	private final double ratioVar;

	private RevInfo(Builder builder) {
		StandardDeviation sd = new StandardDeviation(true);
		cyclo = builder.cyclo;
		cycloVar = sd.evaluate(cyclo) / StatUtils.mean(cyclo);
		volume = builder.volume;
		volumeVar = sd.evaluate(volume) / StatUtils.mean(volume);
		call = builder.call;
		callVar = sd.evaluate(call) / StatUtils.mean(call);
		ratio = builder.ratio;
		ratioVar = sd.evaluate(ratio) / StatUtils.mean(ratio);
	}

	public static class Builder {

		private double[] cyclo;
		private double[] volume;
		private double[] call;
		private double[] ratio;

		public Builder cyclo(final double[] cyclo) {
			this.cyclo = cyclo;
			return this;
		}

		public Builder volume(final double[] volume) {
			this.volume = volume;
			return this;
		}

		public Builder call(final double[] call) {
			this.call = call;
			return this;
		}

		public Builder ratio(final double[] ratio) {
			this.ratio = ratio;
			return this;
		}

		public RevInfo build() {
			return new RevInfo(this);
		}

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
	public double[] getCall() {
		return call;
	}

	@XmlElement
	public double[] getRatio() {
		return ratio;
	}

	@XmlElement
	public double getCycloVar() {
		return cycloVar;
	}

	@XmlElement
	public double getVolumeVar() {
		return volumeVar;
	}

	@XmlElement
	public double getCallVar() {
		return callVar;
	}

	@XmlElement
	public double getRatioVar() {
		return ratioVar;
	}

}
