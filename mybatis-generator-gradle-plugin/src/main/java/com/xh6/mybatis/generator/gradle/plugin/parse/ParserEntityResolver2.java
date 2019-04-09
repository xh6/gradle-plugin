package com.xh6.mybatis.generator.gradle.plugin.parse; /**
 * Copyright 2006-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Jeff Butler
 */
public class ParserEntityResolver2 implements EntityResolver {

    /**
     *
     */
    public ParserEntityResolver2() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     * java.lang.String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("mybatis-generator-config_1_0_new.dtd"); //$NON-NLS-1$
        InputSource ins = new InputSource(is);

        return ins;
    }
}
