// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders;

import webit.script.exceptions.ResourceNotFoundException;

public interface Loader {

    /**
     * get template's Resource by it's name.
     *
     * @param name template's name
     * @return Resource
     * @throws ResourceNotFoundException
     */
    Resource get(String name) throws ResourceNotFoundException;

    /**
     * get child template name by parent template name and relative name.
     *
     * @param parent parent template's name
     * @param name relative name
     * @return child template's name
     */
    String concat(String parent, String name);

    /**
     * normalize a template's name.
     *
     * @param name template's name
     * @return normalized name
     */
    String normalize(String name);
}
