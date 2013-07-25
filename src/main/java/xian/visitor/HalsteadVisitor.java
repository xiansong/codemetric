package xian.visitor;

import java.util.EnumMap;
import java.util.HashMap;

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
 * number of operands. Provide access to Halstead measures targeting at method
 * level.
 * 
 * Visit a single method.
 */
public class HalsteadVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * The Enum ExtraOperator.
	 * 
	 * These two operators have not been defined as operators in javaparser api
	 */
	public static enum ExtraOperator {

		/** The instance of. */
		instanceOf, // instanceof

		/** The ternary. */
		ternary // :?
	}

	/** The oprAsgn stores unique assign operators and their frequencies. */
	private EnumMap<AssignExpr.Operator, Integer> oprAsgn;

	/** The oprBin stores unique binary operators and their frequencies. */
	private EnumMap<BinaryExpr.Operator, Integer> oprBin;

	/** The oprUnary stores unique unary operators and their frequencies. */
	private EnumMap<UnaryExpr.Operator, Integer> oprUnary;

	/** The extraOpr stores unique extra operators and their frequencies. */
	private EnumMap<ExtraOperator, Integer> extraOpr;

	/** The opd stores hashcode of unique operands and their frequencies. */
	private HashMap<Integer, Integer> opd;

	/**
	 * Instantiates a new Halstead visitor.
	 */
	public HalsteadVisitor() {
		oprAsgn = new EnumMap<AssignExpr.Operator, Integer>(
				AssignExpr.Operator.class);
		oprBin = new EnumMap<BinaryExpr.Operator, Integer>(
				BinaryExpr.Operator.class);
		oprUnary = new EnumMap<UnaryExpr.Operator, Integer>(
				UnaryExpr.Operator.class);
		extraOpr = new EnumMap<ExtraOperator, Integer>(ExtraOperator.class);
		opd = new HashMap<Integer, Integer>();
	}

	/**
	 * Visit a variable declaration expression. If there are assignments in it,
	 */
	@Override
	public void visit(final VariableDeclarationExpr n, final Void arg) {
		for (VariableDeclarator vd : n.getVars()) {
			if (vd.getInit() != null) {
				if (oprAsgn.containsKey(AssignExpr.Operator.assign)) {
					oprAsgn.put(AssignExpr.Operator.assign,
							oprAsgn.get(AssignExpr.Operator.assign) + 1);
				} else {
					oprAsgn.put(AssignExpr.Operator.assign, 1);
				}
				if (opd.containsKey(vd.getId().hashCode())) {
					opd.put(vd.getId().hashCode(),
							opd.get(vd.getId().hashCode()) + 1);
				} else {
					opd.put(vd.getId().hashCode(), 1);
				}
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
		if (oprAsgn.containsKey(n.getOperator())) {
			oprAsgn.put(n.getOperator(), oprAsgn.get(n.getOperator()) + 1);
		} else {
			oprAsgn.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getTarget().hashCode())) {
			opd.put(n.getTarget().hashCode(),
					opd.get(n.getTarget().hashCode()) + 1);
		} else {
			opd.put(n.getTarget().hashCode(), 1);
		}
		if (opd.containsKey(n.getValue().hashCode())) {
			opd.put(n.getValue().hashCode(),
					opd.get(n.getValue().hashCode()) + 1);
		} else {
			opd.put(n.getValue().hashCode(), 1);
		}
		n.getTarget().accept(this, arg);
		n.getValue().accept(this, arg);
	}

	/**
	 * Visit a binary expression [+,-,==,&&...], and then visit left-expr and
	 * right-expr with the current visitor.
	 */
	@Override
	public void visit(final BinaryExpr n, final Void arg) {
		if (oprBin.containsKey(n.getOperator())) {
			oprBin.put(n.getOperator(), oprBin.get(n.getOperator()) + 1);
		} else {
			oprBin.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getLeft().hashCode())) {
			opd.put(n.getLeft().hashCode(), opd.get(n.getLeft().hashCode()) + 1);
		} else {
			opd.put(n.getLeft().hashCode(), 1);
		}
		if (opd.containsKey(n.getRight().hashCode())) {
			opd.put(n.getRight().hashCode(),
					opd.get(n.getRight().hashCode()) + 1);
		} else {
			opd.put(n.getRight().hashCode(), 1);
		}
		n.getLeft().accept(this, arg);
		n.getRight().accept(this, arg);
	}

	/**
	 * Visit an unary expression [++,--,+,-,!,~] and then visit the
	 * expression-body with the current visitor
	 */
	@Override
	public void visit(final UnaryExpr n, final Void arg) {
		if (oprUnary.containsKey(n.getOperator())) {
			oprUnary.put(n.getOperator(), oprUnary.get(n.getOperator()) + 1);
		} else {
			oprUnary.put(n.getOperator(), 1);
		}
		if (opd.containsKey(n.getExpr().hashCode())) {
			opd.put(n.getExpr().hashCode(), opd.get(n.getExpr().hashCode()) + 1);
		} else {
			opd.put(n.getExpr().hashCode(), 1);
		}
		n.getExpr().accept(this, arg);
	}

	/**
	 * Visit a ternary expression [boolean?x:y] and then visit its sub-expr.
	 */
	@Override
	public void visit(final ConditionalExpr n, final Void arg) {
		if (extraOpr.containsKey(ExtraOperator.ternary)) {
			extraOpr.put(ExtraOperator.ternary,
					extraOpr.get(ExtraOperator.ternary) + 1);
		} else {
			extraOpr.put(ExtraOperator.ternary, 1);
		}
		n.getCondition().accept(this, arg);
		n.getElseExpr().accept(this, arg);
		n.getThenExpr().accept(this, arg);
	}

	/**
	 * Visit instanceof operation
	 */
	@Override
	public void visit(final InstanceOfExpr n, final Void arg) {
		if (extraOpr.containsKey(ExtraOperator.instanceOf)) {
			extraOpr.put(ExtraOperator.instanceOf,
					extraOpr.get(ExtraOperator.instanceOf) + 1);
		} else {
			extraOpr.put(ExtraOperator.instanceOf, 1);
		}
	}

	/**
	 * Gets the unique operators.
	 * 
	 * @return the unique operators
	 */
	public int getUniqueOperators() {
		return oprAsgn.size() + oprBin.size() + oprUnary.size()
				+ extraOpr.size();
	}

	/**
	 * Gets the total operators.
	 * 
	 * @return the total operators
	 */
	public int getTotalOperators() {
		int total = 0;
		for (Integer i : oprAsgn.values()) {
			total += i;
		}
		for (Integer i : oprBin.values()) {
			total += i;
		}
		for (Integer i : oprUnary.values()) {
			total += i;
		}
		for (Integer i : extraOpr.values()) {
			total += i;
		}
		return total;
	}

	/**
	 * Gets the unique operands.
	 * 
	 * @return the unique operands
	 */
	public int getUniqueOperands() {
		return opd.size();
	}

	/**
	 * Gets the total operands.
	 * 
	 * @return the total operands
	 */
	public int getTotalOperands() {
		int total = 0;
		for (Integer i : opd.values()) {
			total += i;
		}
		return total;
	}

	/**
	 * Gets the vocabulary.
	 * 
	 * @return the vocabulary
	 */
	public int getVocabulary() {
		return getUniqueOperators() + getUniqueOperands();
	}

	/**
	 * Gets the program length.
	 * 
	 * @return the program length
	 */
	public int getProgramLength() {
		return getTotalOperators() + getTotalOperands();
	}

	/**
	 * Gets the volume.
	 * 
	 * @return the volume
	 */
	public double getVolume() {
		if (getVocabulary() == 0)
			return 1;
		return getProgramLength()
				* (Math.log(getVocabulary()) / Math.log(2.0d));
	}

}
