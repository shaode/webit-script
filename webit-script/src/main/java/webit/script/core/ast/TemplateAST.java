// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.Map;
import webit.script.Context;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class TemplateAST {

    private final VariantMap varMap;
    private final Statment[] statments;

    public TemplateAST(VariantMap varMap, Statment[] statments) {
        this.varMap = varMap;
        this.statments = statments;
    }

    public Context execute(final Context context, final Map<String, Object> root) {
        final VariantStack vars = context.vars;
        vars.push(varMap);
        vars.setToCurrentContext(root);
        int len = statments.length;
        for (int i = 0; i < len; i++) {
            StatmentUtil.execute(statments[i], context);
        }
        return context;
    }
}
