// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ArrayValue extends AbstractExpression {

    private final Expression[] valueExprs;

    public ArrayValue(Expression[] valueExprs, int line, int column) {
        super(line, column);
        this.valueExprs = valueExprs;
    }

    public Object execute(final Context context) {
        final Expression[] valueExprs;
        final int len;
        final Object[] value = new Object[len = (valueExprs = this.valueExprs).length];
        int i = 0;
        while (i < len) {
            value[i] = StatmentUtil.execute(valueExprs[i], context);
            i++;
        }
        return value;
    }
}
