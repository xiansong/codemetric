package xian.visitor;

import java.util.HashMap;
import java.util.List;

import xian.model.UserClass;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * The Class ClassVisitor.
 * 
 * Count only the main class in a java file because the inner class or private
 * class usually won't have directly relation with the outside world.
 */
public final class ClassVisitor extends VoidVisitorAdapter<Void> {

	/**
	 * The package declaration is retreive from the complilation unit of a java
	 * file.
	 */
	private PackageDeclaration packageDeclaration;

	private UserClass userClass;

	/**
	 * The import packages are retreive from the complilation unit of a java
	 * file.
	 */
	private List<ImportDeclaration> imports;

	public ClassVisitor() {
	}

	public void setPkgDeclaration(final PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	public void setImports(final List<ImportDeclaration> imports) {
		this.imports = imports;
	}

	@Override
	public void visit(final ClassOrInterfaceDeclaration n, final Void arg) {
		if (!n.isInterface()) {
			// pre-check the fields in the class
			FieldVisitor fv = new FieldVisitor();
			n.accept(fv, arg);
			userClass = new UserClass(packageDeclaration, n.getName(), imports);
			userClass.setFields(fv.fields);
			fv = null; // reference explicitly set to null
			MethodVisitor mv = new MethodVisitor();
			n.accept(mv, arg);
			userClass.setDefinedMethods(mv.getMethodList());
		}
	}

	public UserClass getUserClass() {
		return userClass;
	}

	private static class FieldVisitor extends VoidVisitorAdapter<Void> {

		HashMap<String, String> fields;

		public FieldVisitor() {
			fields = new HashMap<String, String>();
		}

		@Override
		public void visit(final FieldDeclaration n, final Void arg) {
			for (VariableDeclarator v : n.getVariables()) {
				Boolean isRefType = n.getType().accept(
						new GenericVisitorAdapter<Boolean, Void>() {
							@Override
							public Boolean visit(final ReferenceType n,
									final Void arg) {
								return Boolean.TRUE;
							}
						}, null);
				if (isRefType != null)
					fields.put(v.getId().toString(), n.getType().toString());
			}
		}

	}

}
