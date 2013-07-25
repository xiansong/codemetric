package xian.model;

import japa.parser.ast.expr.MethodCallExpr;

import java.util.HashMap;
import java.util.List;

/**
 * Stores information of a method in the user-defined classes.
 */
public final class UserMethod {

	private String methodName;

	private String returnType;

	/**
	 * the Cyclomatic number of this method
	 */
	private int cylomatic;

	/**
	 * the Halstead volume
	 */
	private double volume;

	private HashMap<String, String> parameters;

	private HashMap<String, String> variables;

	private List<MethodCallExpr> calls;

	public UserMethod(final String methodName,final String returnType) {
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

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(final HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public HashMap<String, String> getVariables() {
		return variables;
	}

	public void setVariables(final HashMap<String, String> variables) {
		this.variables = variables;
	}

}
