package xian.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xian.model.UserMethod;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class MethodVisitor.
 */
public class MethodVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * The method list.
	 * 
	 * Store a list methods defined in the class visited.
	 */
	private List<UserMethod> methodList;

	public MethodVisitor() {
		methodList = new ArrayList<UserMethod>();
	}

	@Override
	public void visit(final MethodDeclaration n, final Void arg) {

		UserMethod method = new UserMethod(n.getName(), n.getType().toString());

		// get method parameter and method variables
		ParameterVisitor pv = new ParameterVisitor();
		n.accept(pv, arg);
		method.setParameters(pv.parameters);
		pv = null; // reference explicitly set to null

		VariableVisitor vv = new VariableVisitor();
		n.accept(vv, arg);
		method.setVariables(vv.variables);
		vv = null;// reference explicitly set to null

		// get method calls
		CallVisitor cav = new CallVisitor();
		n.accept(cav, arg);
		method.setCalls(cav.calls);
		cav = null;// reference explicitly set to null

		// get Cyclomatic
		CyclomaticVisitor cv = new CyclomaticVisitor();
		n.accept(cv, arg);
		method.setCylomatic(cv.getCyclomaticNumber());
		cv = null;// reference explicitly set to null

		// get Halstead
		HalsteadVisitor hv = new HalsteadVisitor();
		n.accept(hv, arg);
		method.setVolume(hv.getVolume());
		hv = null; // pv reference explicitly set to null

		methodList.add(method);

	}

	public List<UserMethod> getMethodList() {
		return methodList;
	}

	private final class ParameterVisitor extends VoidVisitorAdapter<Void> {

		private HashMap<String, String> parameters;

		public ParameterVisitor() {
			parameters = new HashMap<String, String>();
		}

		@Override
		public void visit(final Parameter n, final Void arg) {
			Boolean isRefType = n.getType().accept(
					new GenericVisitorAdapter<Boolean, Void>() {
						@Override
						public Boolean visit(final ReferenceType n,
								final Void arg) {
							return Boolean.TRUE;
						}
					}, arg);
			if (isRefType != null)
				parameters.put(n.getId().toString(), n.getType().toString());
		}

	}

	/**
	 * The Class VariableVisitor.
	 * 
	 * Counts local variable (excluding primitive types).
	 */
	private final class VariableVisitor extends VoidVisitorAdapter<Void> {

		private HashMap<String, String> variables;

		public VariableVisitor() {
			variables = new HashMap<String, String>();
		}

		@Override
		public void visit(final VariableDeclarationExpr n, final Void arg) {
			for (VariableDeclarator v : n.getVars()) {
				Boolean isRefType = n.getType().accept(
						new GenericVisitorAdapter<Boolean, Void>() {
							@Override
							public Boolean visit(final ReferenceType n,
									final Void arg) {
								return Boolean.TRUE;
							}
						}, arg);
				if (isRefType != null)
					variables.put(v.getId().toString(), n.getType().toString());
			}
		}

	}

	/**
	 * The Class CallVisitor.
	 * 
	 * Counts method calls within a method, the methodcall may contain nested
	 * call.
	 */
	private final class CallVisitor extends VoidVisitorAdapter<Void> {

		private List<MethodCallExpr> calls;

		public CallVisitor() {
			calls = new ArrayList<MethodCallExpr>();
		}

		@Override
		public void visit(final MethodCallExpr n, final Void arg) {
			calls.add(n);
		}

	}

}
