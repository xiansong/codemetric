package xian.visitor.model;

import japa.parser.ast.expr.MethodCallExpr;

import java.util.List;
import java.util.Map;

/**
 * Stores information of a method in the user-defined classes.
 */
public final class UserMethod {

	private final String methodName;

	private final String returnType;

	private int cylomatic;

	private double volume;

	private Map<String, String> parameters;

	private Map<String, String> variables;

	private List<MethodCallExpr> calls;

	public UserMethod(final String methodName, final String returnType) {
		this.methodName = methodName;
		this.returnType = returnType;
	}

	public int getCylomatic() {
		return cylomatic;
	}

	public void setCylomatic(final int cylomatic) {
		this.cylomatic = cylomatic;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(final double volume) {
		this.volume = volume;
	}

	public String getName() {
		return methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setCalls(final List<MethodCallExpr> calls) {
		this.calls = calls;
	}

	public List<MethodCallExpr> getCalls() {
		return calls;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(final Map<String, String> variables) {
		this.variables = variables;
	}

	public double getRatio() {
		if (volume == 0.0)
			return 1;
		return cylomatic / volume;
	}

}
