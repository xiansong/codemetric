package xian.model;

/**
 * 
 * Method invocations relationship.
 */
public class CallModel {

	private final String caller;
	private final String callee;
	private final int callerCyclo;
	private final double callerVolume;
	private final int calleeCyclo;
	private final double calleeVolume;

	/**
	 * Nested class to build an instance of CallModel.
	 */
	public static class Builder {
		private final String caller;
		private final String callee;

		// default value of metric
		private int callerCyclo = 1;
		private int calleeCyclo = 1;
		private double callerVolume = 1;
		private double calleeVolume = 1;

		public Builder callerCyclo(final int cyclo) {
			callerCyclo = cyclo;
			return this;
		}

		public Builder calleeCyclo(final int cyclo) {
			calleeCyclo = cyclo;
			return this;
		}

		public Builder callerVolume(final double volume) {
			callerVolume = volume;
			return this;
		}

		public Builder calleeVolume(final double volume) {
			calleeVolume = volume;
			return this;
		}

		public Builder(final String caller, final String callee) {
			this.caller = caller;
			this.callee = callee;
		}

		public CallModel build() {
			return new CallModel(this);
		}
	}

	// private constructor for the nested class to instantiate it
	private CallModel(final Builder builder) {
		caller = builder.caller;
		callee = builder.callee;
		callerCyclo = builder.callerCyclo;
		callerVolume = builder.callerVolume;
		calleeCyclo = builder.calleeCyclo;
		calleeVolume = builder.calleeVolume;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CallModel [caller=").append(caller).append(",callee=")
				.append(callee).append(",callerCyclo=").append(callerCyclo)
				.append(",callerVolume=").append(callerVolume)
				.append(",calleeCyclo=").append(calleeCyclo)
				.append(",calleeVolume=").append(calleeVolume).append("]");
		return sb.toString();
	}

	private volatile int hashCode;

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = caller.hashCode() + callee.hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(final Object callModel) {
		if (callModel == this)
			return true;
		if (!(callModel instanceof CallModel))
			return false;
		CallModel cm = (CallModel) callModel;
		if (cm.getCaller().equals(caller) && cm.getCallee().equals(callee)) {
			return true;
		} else {
			return false;
		}
	}

	public String getCaller() {
		return caller;
	}

	public String getCallee() {
		return callee;
	}

	public int getCallerCyclo() {
		return callerCyclo;
	}

	public double getCallerVolume() {
		return callerVolume;
	}

	public int getCalleeCyclo() {
		return calleeCyclo;
	}

	public double getCalleeVolume() {
		return calleeVolume;
	}

}
