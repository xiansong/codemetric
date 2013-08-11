package xian.visitor;

import gnu.trove.map.hash.TObjectIntHashMap;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class HalsteadVisitor.
 * 
 * Check unique operators, operands and total number of operators and total
 * number of operands.
 */
public final class HalsteadVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * These two operators have not been defined as operators in javaparser api
	 */
	public static enum ExtraOperator {

		/** The instance of. */
		instanceOf,

		/** The ternary [:?]. */
		ternary
	}

	/** The oprAsgn stores unique assign operators and their frequencies. */
	private final int[] oprAsgn;

	/** The oprBin stores unique binary operators and their frequencies. */
	private final int[] oprBin;

	/** The oprUnary stores unique unary operators and their frequencies. */
	private final int[] oprUnary;

	/** The extraOpr stores unique extra operators and their frequencies. */
	private final int[] extraOpr;

	/** The opd stores hashcode of unique operands and their frequencies. */
	private final TObjectIntHashMap<String> opd;

	public HalsteadVisitor() {
		oprAsgn = new int[AssignExpr.Operator.values().length];
		oprBin = new int[BinaryExpr.Operator.values().length];
		oprUnary = new int[UnaryExpr.Operator.values().length];
		extraOpr = new int[ExtraOperator.values().length];
		opd = new TObjectIntHashMap<String>();
	}

	/**
	 * Visit a variable declaration expression. If there are assignments in it,
	 */
	@Override
	public void visit(final VariableDeclarationExpr n, final Void arg) {
		for (VariableDeclarator vd : n.getVars()) {
			if (vd.getInit() != null) {
				oprAsgn[AssignExpr.Operator.assign.ordinal()]++;
				opd.adjustOrPutValue(vd.getId().getName(), 1, 1);
				vd.accept(this, arg);
			}

		}
	}

	/**
	 * Visit an assign expression [=,+=,-=...], collect operators and operands
	 * and then visit the target and value by the current visitor.
	 */
	@Override
	public void visit(final AssignExpr n, final Void arg) {
		oprAsgn[n.getOperator().ordinal()]++;
		opd.adjustOrPutValue(n.getTarget().toString(), 1, 1);
		opd.adjustOrPutValue(n.getValue().toString(), 1, 1);
		n.getTarget().accept(this, arg);
		n.getValue().accept(this, arg);
	}

	/**
	 * Visit a binary expression [+,-,==,&&...], and then visit left-expr and
	 * right-expr with the current visitor.
	 */
	@Override
	public void visit(final BinaryExpr n, final Void arg) {
		oprBin[n.getOperator().ordinal()]++;
		opd.adjustOrPutValue(n.getLeft().toString(), 1, 1);
		opd.adjustOrPutValue(n.getRight().toString(), 1, 1);
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
	}

	/**
	 * Visit an unary expression [++,--,+,-,!,~] and then visit the
	 * expression-body with the current visitor
	 */
	@Override
	public void visit(final UnaryExpr n, final Void arg) {
		oprUnary[n.getOperator().ordinal()]++;
		opd.adjustOrPutValue(n.getExpr().toString(), 1, 1);
		n.getExpr().accept(this, arg);
	}

	/**
	 * Visit a ternary expression [boolean?x:y] and then visit its sub-expr.
	 */
	@Override
	public void visit(final ConditionalExpr n, final Void arg) {
		extraOpr[ExtraOperator.ternary.ordinal()]++;
		n.getCondition().accept(this, arg);
		n.getElseExpr().accept(this, arg);
		n.getThenExpr().accept(this, arg);
	}

	/**
	 * Visit instanceof operation
	 */
	@Override
	public void visit(final InstanceOfExpr n, final Void arg) {
		extraOpr[ExtraOperator.instanceOf.ordinal()]++;
	}

	public int getUniqueOperators() {
		int sum = 0;
		for (int i : oprAsgn) {
			if (i != 0) {
				sum++;
			}
		}
		for (int i : oprBin) {
			if (i != 0) {
				sum++;
			}
		}
		for (int i : oprUnary) {
			if (i != 0) {
				sum++;
			}
		}
		for (int i : extraOpr) {
			if (i != 0) {
				sum++;
			}
		}
		return sum;

	}

	public int getTotalOperators() {
		int total = 0;
		for (int i : oprAsgn) {
			total += i;
		}
		for (int i : oprBin) {
			total += i;
		}
		for (int i : oprUnary) {
			total += i;
		}
		for (int i : extraOpr) {
			total += i;
		}
		return total;
	}

	public int getUniqueOperands() {
		return opd.size();
	}

	public int getTotalOperands() {
		int total = 0;
		for (int i : opd.values()) {
			total += i;
		}
		return total;
	}

	public int getVocabulary() {
		return getUniqueOperators() + getUniqueOperands();
	}

	public int getProgramLength() {
		return getTotalOperators() + getTotalOperands();
	}

	public double getVolume() {
		if (getVocabulary() == 0 || getProgramLength() == 0)
			return 1.0;
		return getProgramLength() * (Math.log(getVocabulary()) / Math.log(2.0));
	}

}
