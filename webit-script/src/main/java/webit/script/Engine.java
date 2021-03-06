// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.core.text.TextStatmentFactory;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.filters.Filter;
import webit.script.io.charset.CoderFactory;
import webit.script.loaders.Loader;
import webit.script.loggers.Logger;
import webit.script.resolvers.Resolver;
import webit.script.resolvers.ResolverManager;
import webit.script.security.NativeSecurityManager;
import webit.script.util.EncodingPool;
import webit.script.util.Petite;
import webit.script.util.PropsUtil;
import webit.script.util.StringUtil;
import webit.script.util.props.Props;

/**
 *
 * @author Zqq
 */
public final class Engine {

    //
    private static final String DEFAULT_PROPERTIES = "/webit-script-default.props";
    //settings
    private Class resourceLoaderClass = webit.script.loaders.impl.ClasspathLoader.class;
    private Class textStatmentFactoryClass = webit.script.core.text.impl.SimpleTextStatmentFactory.class;
    private Class nativeSecurityManagerClass = webit.script.security.impl.DefaultNativeSecurityManager.class;
    private Class coderFactoryClass = webit.script.io.charset.impl.DefaultCoderFactory.class;
    private Class filterClass;
    private Class loggerClass = webit.script.loggers.impl.NOPLogger.class;
    private Class[] resolvers;
    private String encoding = EncodingPool.UTF_8;
    private boolean enableAsmNative = true;
    private boolean looseVar = false;
    private boolean trimCodeBlockBlankLine = true;
    private boolean appendLostFileNameExtension = false;
    private String fileNameExtension = ".wtl";
    //
    private Logger logger;
    private Loader resourceLoader;
    private TextStatmentFactory textStatmentFactory;
    private NativeSecurityManager nativeSecurityManager;
    private CoderFactory coderFactory;
    private Filter filter;
    //
    private final ResolverManager resolverManager;
    private final ConcurrentMap<String, Template> templateCache;
    private final Petite _petite;

    private Engine(Petite petite) {
        this._petite = petite;
        this.templateCache = new ConcurrentHashMap<String, Template>();
        this.resolverManager = new ResolverManager();
    }

    @SuppressWarnings("unchecked")
    private void init() throws Exception {

        this.logger = (Logger) newInstance(this.loggerClass);
        this.coderFactory = (CoderFactory) newInstance(this.coderFactoryClass);
        this.nativeSecurityManager = (NativeSecurityManager) newInstance(this.nativeSecurityManagerClass);
        this.textStatmentFactory = (TextStatmentFactory) newInstance(this.textStatmentFactoryClass);
        this.resourceLoader = (Loader) newInstance(this.resourceLoaderClass);

        if (this.filterClass != null) {
            this.filter = (Filter) newInstance(this.filterClass);
        }

        resolveBean(this.logger);
        resolveBean(this.resolverManager);
        resolveBean(this.coderFactory);
        resolveBean(this.nativeSecurityManager);
        resolveBean(this.textStatmentFactory);
        resolveBean(this.resourceLoader);
        if (this.filter != null) {
            resolveBean(this.filter);
        }
        resolveBean(this.resolverManager);

        if (this.resolvers != null) {
            final int resolversLength;
            Resolver[] resolverInstances = new Resolver[resolversLength = this.resolvers.length];
            for (int i = 0; i < resolversLength; i++) {
                resolverInstances[i] = (Resolver) getBean(this.resolvers[i]);
            }
            this.resolverManager.init(resolverInstances);
        }
    }

    public void resolveBean(Object bean) {
        _petite.wireBean(bean);
        if (bean instanceof Initable) {
            ((Initable) bean).init(this);
        }
    }

    private Object newInstance(Class type) throws InstantiationException, IllegalAccessException {
        return type.newInstance();
    }

    public Object getBean(Class type) throws InstantiationException, IllegalAccessException {
        Object bean;
        resolveBean(bean = newInstance(type));
        return bean;
    }

    /**
     * get template by parent template's name and it's relative name.
     *
     * @param parentName parent template's name
     * @param name template's relative name
     * @return Template
     * @throws ResourceNotFoundException
     */
    public Template getTemplate(String parentName, String name) throws ResourceNotFoundException {
        return getTemplate(resourceLoader.concat(parentName, name));
    }

    /**
     * get template by name.
     *
     * @param name template's name
     * @return Template
     * @throws ResourceNotFoundException
     */
    public Template getTemplate(final String name) throws ResourceNotFoundException {
        Template template;
        if ((template = templateCache.get(name)) != null) {
            return template;
        } else {
            return createTemplateIfAbsent(name);
        }
    }

    private Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final String normalizedName;
        if ((normalizedName = resourceLoader.normalize(name)) != null) {
            if ((template = templateCache.get(normalizedName)) == null) {
                Template oldTemplate;
                if ((oldTemplate = templateCache.putIfAbsent(normalizedName,
                        template = new Template(this, normalizedName,
                                resourceLoader.get(normalizedName)))) != null) {
                    template = oldTemplate;
                }
                if (!name.equals(normalizedName)
                        && (oldTemplate = templateCache.putIfAbsent(name, template)) != null) {
                    template = oldTemplate;
                }
            }
        } else {
            throw new ResourceNotFoundException("Illegal template path".concat(name));
        }
        return template;
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
    }

    public void setNativeSecurityManagerClass(Class nativeSecurityManagerClass) {
        this.nativeSecurityManagerClass = nativeSecurityManagerClass;
    }

    public void setCoderFactoryClass(Class coderFactoryClass) {
        this.coderFactoryClass = coderFactoryClass;
    }

    public void setResolvers(Class[] resolvers) {
        this.resolvers = resolvers;
    }

    public void setResourceLoaderClass(Class resourceLoaderClass) {
        this.resourceLoaderClass = resourceLoaderClass;
    }

    public void setTextStatmentFactoryClass(Class textStatmentFactoryClass) {
        this.textStatmentFactoryClass = textStatmentFactoryClass;
    }

    public void setResourceLoader(Loader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding != null) {
            this.encoding = EncodingPool.intern(encoding);
        }
    }

    public boolean isEnableAsmNative() {
        return enableAsmNative;
    }

    public void setEnableAsmNative(boolean enableAsmNative) {
        this.enableAsmNative = enableAsmNative;
    }

    public boolean isLooseVar() {
        return looseVar;
    }

    public void setLooseVar(boolean looseVar) {
        this.looseVar = looseVar;
    }

    public boolean isTrimCodeBlockBlankLine() {
        return trimCodeBlockBlankLine;
    }

    public void setTrimCodeBlockBlankLine(boolean trimCodeBlockBlankLine) {
        this.trimCodeBlockBlankLine = trimCodeBlockBlankLine;
    }

    public NativeSecurityManager getNativeSecurityManager() {
        return nativeSecurityManager;
    }

    public ResolverManager getResolverManager() {
        return resolverManager;
    }

    public TextStatmentFactory getTextStatmentFactory() {
        return textStatmentFactory;
    }

    public CoderFactory getCoderFactory() {
        return coderFactory;
    }

    public void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setLoggerClass(Class loggerClass) {
        this.loggerClass = loggerClass;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isAppendLostFileNameExtension() {
        return appendLostFileNameExtension;
    }

    public void setAppendLostFileNameExtension(boolean appendLostFileNameExtension) {
        this.appendLostFileNameExtension = appendLostFileNameExtension;
    }

    public String getFileNameExtension() {
        return fileNameExtension;
    }

    public void setFileNameExtension(String fileNameExtension) {
        this.fileNameExtension = fileNameExtension;
    }

    public static Engine createEngine(String configPath) {
        return createEngine(configPath, null);
    }

    public static Engine createEngine(String configPath, Map<Object, Object> parameters) {

        final Props props = new Props();

        final List<String> propsFiles;
        propsFiles = configPath != null
                ? PropsUtil.loadFromClasspath(props, DEFAULT_PROPERTIES, configPath)
                : PropsUtil.loadFromClasspath(props, DEFAULT_PROPERTIES);

        final Map<String, Object> extraDirectParameters = new HashMap<String, Object>();
        if (parameters != null) {
            String name;
            Object value;
            for (Map.Entry entry : parameters.entrySet()) {
                name = String.valueOf(entry.getKey());
                value = entry.getValue();
                if (value instanceof String) {
                    props.load(name, (String) value);
                } else {
                    extraDirectParameters.put(name, value);
                }
            }
        }

        final Petite petite = new Petite();
        petite.defineParameters(props, extraDirectParameters);

        final Engine engine;
        petite.wireBean(engine = new Engine(petite));

        try {
            engine.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //Log props file name
        final Logger logger;
        petite.setLogger(logger = engine.getLogger());
        if (logger != null && logger.isInfoEnabled()) {
            logger.info("Loaded props files from classpath: ".concat(StringUtil.join(propsFiles, ", ")));
        }

        return engine;
    }
}
