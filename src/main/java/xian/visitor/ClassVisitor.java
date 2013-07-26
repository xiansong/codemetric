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
	 * Package declaration is retrieve from the compilation unit of a java file.
	 */
	private PackageDeclaration packageDeclaration;

	/**
	 * Import packages are retrieve from the compilation unit of a java file.
	 */
	private List<ImportDeclaration> imports;

	private UserClass userClass;

	public ClassVisitor() {
	}

	/**
	 * Sets the package declarations for the class visitor.
	 * 
	 * @param packageDeclaration
	 *            package declarations from the compilation unit, may be null
	 */
	public void setPkgDeclaration(final PackageDeclaration packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	/**
	 * Sets the imports for the class visitor.
	 * 
	 * @param imports
	 *            imports from the compilation unit, may be null
	 */
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

	/**
	 * Gets the userClass from the current class visitor.
	 * 
	 * @return the user class
	 */
	public UserClass getUserClass() {
		return userClass;
	}

	/**
	 * The Class FieldVisitor.
	 * 
	 * Counts instance variables in the class (excluding primitive types).
	 */
	private static class FieldVisitor extends VoidVisitorAdapter<Void> {

		/**
		 * Fields of the class visited, name as key and type as value in the map
		 */
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
