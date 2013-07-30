package xian.visitor;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.MethodCallExpr;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import xian.visitor.model.CallModel;
import xian.visitor.model.CommitData;
import xian.visitor.model.UserClass;
import xian.visitor.model.UserMethod;

public final class CommitVisitor implements Callable<CommitData> {

	private List<UserClass> ucs;
	private Set<CallModel> cms;

	public CommitVisitor(final List<CompilationUnit> cus) {
		ucs = Lists.newArrayList();
		cms = Sets.newHashSet();
		for (Iterator<CompilationUnit> itr = cus.iterator(); itr.hasNext();) {
			visit(itr.next());
		}
	}

	private void visit(final CompilationUnit cu) {

		ClassVisitor visitor = new ClassVisitor();
		visitor.setPkgDeclaration(cu.getPackage());
		visitor.setImports(cu.getImports());

		cu.accept(visitor, null);

		UserClass uc = visitor.getUserClass();
		if (uc != null)
			ucs.add(uc);
	}

	@Override
	public CommitData call() throws Exception {

		for (UserClass uc : ucs) {
			for (UserMethod um : uc.getDefinedMethods()) {
				// System.out.println(um.getName());
				for (MethodCallExpr mc : um.getCalls()) {
					CallModel cm = checkCallModel(mc, um, uc);
					if (cm != null) {
						cms.add(cm);
					}
				}

			}
		}
		CommitData cd = new CommitData();
		cd.setCms(cms);
		cd.setUcs(ucs);
		return cd;
	}

	private CallModel checkCallModel(final MethodCallExpr mc,
			final UserMethod um, final UserClass uc) {
		// check if the callee variable is defined in method parameters
		if (mc.getScope() != null
				&& um.getParameters().containsKey(mc.getScope().toString())) {

			// check whether the callee type is in the import class list
			for (ImportDeclaration n : uc.getImports()) {
				if (n.getName()
						.getName()
						.equals(um.getParameters()
								.get(mc.getScope().toString()))) {
					// check if the import class is user-defined class
					for (UserClass ucc : ucs) {
						if (ucc.getCanonicalName().equals(
								n.getName().toString())) {
							UserMethod callee = ucc.getUserMethod(mc.getName());
							if (callee == null)
								return null;

							StringBuilder sbCaller = new StringBuilder();
							StringBuilder sbCallee = new StringBuilder();
							sbCaller.append(uc.getCanonicalName()).append(".")
									.append(um.getName());
							sbCallee.append(ucc.getCanonicalName()).append(".")
									.append(callee.getName());

							CallModel cm = new CallModel.Builder(
									sbCaller.toString(), sbCallee.toString())
									.callerCyclo(um.getCylomatic())
									.callerVolume(um.getVolume())
									.calleeCyclo(callee.getCylomatic())
									.calleeVolume(callee.getVolume()).build();
							return cm;
						}
					}
					return null;
				}
			}
			for (UserClass ucc : ucs) {
				if (ucc.getName().equals(
						um.getParameters().get(mc.getScope().toString()))) {
					UserMethod callee = ucc.getUserMethod(mc.getName());
					if (callee == null)
						return null;

					StringBuilder sbCaller = new StringBuilder();
					StringBuilder sbCallee = new StringBuilder();
					sbCaller.append(uc.getCanonicalName()).append(".")
							.append(um.getName());
					sbCallee.append(ucc.getCanonicalName()).append(".")
							.append(callee.getName());

					CallModel cm = new CallModel.Builder(sbCaller.toString(),
							sbCallee.toString()).callerCyclo(um.getCylomatic())
							.callerVolume(um.getVolume())
							.calleeCyclo(callee.getCylomatic())
							.calleeVolume(callee.getVolume()).build();
					return cm;
				}
			}

			// check if the callee variable is defined in local variables
		} else if (mc.getScope() != null
				&& um.getVariables().containsKey(mc.getScope().toString())) {
			// check if the callee type is in the import list
			for (ImportDeclaration n : uc.getImports()) {
				if (n.getName()
						.getName()
						.equals(um.getVariables().get(mc.getScope().toString()))) {
					// check if the import type is user-defined class
					for (UserClass ucc : ucs) {
						if (ucc.getCanonicalName().equals(
								n.getName().toString())) {
							UserMethod callee = uc.getUserMethod(mc.getName());
							if (callee == null)
								return null;

							StringBuilder sbCaller = new StringBuilder();
							StringBuilder sbCallee = new StringBuilder();
							sbCaller.append(uc.getCanonicalName()).append(".")
									.append(um.getName());
							sbCallee.append(ucc.getCanonicalName()).append(".")
									.append(callee.getName());

							CallModel cm = new CallModel.Builder(
									sbCaller.toString(), sbCallee.toString())
									.callerCyclo(um.getCylomatic())
									.callerVolume(um.getVolume())
									.calleeCyclo(callee.getCylomatic())
									.calleeVolume(callee.getVolume()).build();

							return cm;
						}
					}
					return null;
				}
			}
			for (UserClass ucc : ucs) {
				if (ucc.getName().equals(
						um.getVariables().get(mc.getScope().toString()))) {
					UserMethod callee = ucc.getUserMethod(mc.getName());
					if (callee == null)
						return null;
					StringBuilder sbCaller = new StringBuilder();
					StringBuilder sbCallee = new StringBuilder();
					sbCaller.append(uc.getCanonicalName()).append(".")
							.append(um.getName());
					sbCallee.append(ucc.getCanonicalName()).append(".")
							.append(callee.getName());
					CallModel cm = new CallModel.Builder(sbCaller.toString(),
							sbCallee.toString()).callerCyclo(um.getCylomatic())
							.callerVolume(um.getVolume())
							.calleeCyclo(callee.getCylomatic())
							.calleeVolume(callee.getVolume()).build();
					return cm;
				}
			}

			// check if the callee variable is defined in instance variables
		} else if (mc.getScope() != null
				&& uc.getFields().containsKey(mc.getScope().toString())) {
			// check if the callee type is in the import list
			for (ImportDeclaration n : uc.getImports()) {
				if (n.getName().getName()
						.equals(uc.getFields().get(mc.getScope().toString()))) {
					// check if the import type is user-defined class
					for (UserClass ucc : ucs) {
						if (ucc.getCanonicalName().equals(
								n.getName().toString())) {
							UserMethod callee = ucc.getUserMethod(mc.getName());
							if (callee == null)
								return null;
							StringBuilder sbCaller = new StringBuilder();
							StringBuilder sbCallee = new StringBuilder();
							sbCaller.append(uc.getCanonicalName()).append(".")
									.append(um.getName());
							sbCallee.append(ucc.getCanonicalName()).append(".")
									.append(callee.getName());
							CallModel cm = new CallModel.Builder(
									sbCaller.toString(), sbCallee.toString())
									.callerCyclo(um.getCylomatic())
									.callerVolume(um.getVolume())
									.calleeCyclo(callee.getCylomatic())
									.calleeVolume(callee.getVolume()).build();
							return cm;
						}
					}
					return null;
				}
			}
			for (UserClass ucc : ucs) {
				if (ucc.getName().equals(mc.getName())) {
					UserMethod callee = ucc.getUserMethod(mc.getName());
					if (callee == null)
						return null;
					StringBuilder sbCaller = new StringBuilder();
					StringBuilder sbCallee = new StringBuilder();
					sbCaller.append(uc.getCanonicalName()).append(".")
							.append(um.getName());
					sbCallee.append(ucc.getCanonicalName()).append(".")
							.append(callee.getName());
					CallModel cm = new CallModel.Builder(sbCaller.toString(),
							sbCallee.toString()).callerCyclo(um.getCylomatic())
							.callerVolume(um.getVolume())
							.calleeCyclo(callee.getCylomatic())
							.calleeVolume(callee.getVolume()).build();
					return cm;
				}
			}
		}

		return null;
	}

}
