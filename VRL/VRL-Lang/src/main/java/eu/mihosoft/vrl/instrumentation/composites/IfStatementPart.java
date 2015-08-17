package eu.mihosoft.vrl.instrumentation.composites;

import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;

import eu.mihosoft.vrl.instrumentation.transform.TransformContext;
import eu.mihosoft.vrl.lang.model.CodeLineColumnMapper;
import eu.mihosoft.vrl.lang.model.ControlFlowScope;
import eu.mihosoft.vrl.lang.model.IArgument;
import eu.mihosoft.vrl.lang.model.IfDeclaration;
import eu.mihosoft.vrl.lang.model.VisualCodeBuilder;

public class IfStatementPart extends
		AbstractCodeBuilderPart<IfStatement, IfDeclaration, ControlFlowScope> {

	public IfStatementPart(SourceUnit sourceUnit, VisualCodeBuilder builder,
			CodeLineColumnMapper mapper) {
		super(sourceUnit, builder, mapper);
	}

	@Override
	public IfDeclaration transform(IfStatement s,
			ControlFlowScope currentScope, TransformContext context) {

		IArgument condition = convertToArgument("IfStatement.condition",
				s.getBooleanExpression(), context);

		IfDeclaration decl = null;

		if (currentScope instanceof IfDeclaration) {
			decl = builder.invokeElseIf(
					(ControlFlowScope) currentScope.getParent(), condition);
		} else {
			decl = builder.invokeIf(currentScope, condition);
		}

		setCodeRange(decl, s);
		addCommentsToScope(decl, comments);
		return decl;
	}

	private boolean hasUnconditionalElseBlock(IfStatement in) {
		Statement elseBlock = in.getElseBlock();
		return (elseBlock != null || elseBlock instanceof EmptyStatement)
				&& !(elseBlock instanceof IfStatement);
	}

	@Override
	public void postTransform(IfDeclaration obj, IfStatement in,
			ControlFlowScope parent, TransformContext context) {

		if (in.getBooleanExpression().getExpression() == null) {
			throwErrorMessage("if-statement: must contain boolean"
					+ " expression!", in.getBooleanExpression());
		}

		if (!(parent instanceof ControlFlowScope)) {
			throwErrorMessage(
					"If-Statement can only be invoked inside ControlFlowScopes!",
					in);
		}
	}

	@Override
	public Class<IfStatement> getAcceptedType() {
		return IfStatement.class;
	}

	@Override
	public Class<ControlFlowScope> getParentType() {
		return ControlFlowScope.class;
	}
}