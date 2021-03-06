// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.util.ALU;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class NegativeOperator extends AbstractExpression implements Optimizable {

    private final Expression expr;

    public NegativeOperator(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        return ALU.negative(StatmentUtil.execute(expr, context));
    }

    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.negative(((DirectValue) expr).value), line, column)
                : this;
    }
}
