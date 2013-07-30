package xian.visitor;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Map;

import xian.visitor.model.UserMethod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The Class MethodVisitor.
 */
public final class MethodVisitor extends VoidVisitorAdapter<Void> {

	private List<UserMethod> methodList;

	public MethodVisitor() {
		methodList = Lists.newArrayList();
	}

	@Override
	public void visit(final MethodDeclaration n, final Void arg) {
		UserMethod method = new UserMethod(n.getName(), n.getType().toString());

		ParameterVisitor pv = new ParameterVisitor();
		n.accept(pv, arg);
		method.setParameters(pv.parameters);
		pv = null;

		VariableVisitor vv = new VariableVisitor();
		n.accept(vv, arg);
		method.setVariables(vv.variables);
		vv = null;

		CallVisitor cav = new CallVisitor();
		n.accept(cav, arg);
		method.setCalls(cav.calls);
		cav = null;

		CyclomaticVisitor cv = new CyclomaticVisitor();
		n.accept(cv, arg);
		method.setCylomatic(cv.getCyclomaticNumber());
		cv = null;

		HalsteadVisitor hv = new HalsteadVisitor();
		n.accept(hv, arg);
		method.setVolume(hv.getVolume());
		hv = null;

		methodList.add(method);
	}

	public List<UserMethod> getMethodList() {
		return methodList;
	}

	/**
	 * The Class ParameterVisitor.
	 * 
	 * Counts parameters in the method visited (excluding primitive types).
	 */
	private static final class ParameterVisitor extends VoidVisitorAdapter<Void> {

		/** The parameters, name as key and type as value, won't be null. */
		private Map<String, String> parameters;

		public ParameterVisitor() {
			parameters = Maps.newHashMap();
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
	private static final class VariableVisitor extends VoidVisitorAdapter<Void> {

		/** The variables, name as key and type as value, won't be null. */
		private Map<String, String> variables;

		public VariableVisitor() {
			variables = Maps.newHashMap();
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
	 * Counts method calls within a method, the method call may contain nested
	 * call.
	 */
	private static final class CallVisitor extends VoidVisitorAdapter<Void> {

		/**
		 * The calls is the whole call expression. It will be analyzed when a
		 * symbol table is built.
		 */
		private List<MethodCallExpr> calls;

		public CallVisitor() {
			calls = Lists.newArrayList();
		}

		@Override
		public void visit(final MethodCallExpr n, final Void arg) {
			calls.add(n);
		}

	}

}
