/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.bigbird0101.code.core.spi;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Type based SPI service loader.
 *
 * @param <T> type of algorithm class
 * @author Administrator
 */
public abstract class TypeBasedSPIServiceLoader<T extends TypeBasedSPI> {

    private final Class<T> classType;

    protected TypeBasedSPIServiceLoader(Class<T> classType) {
        this.classType = classType;
    }

    /**
     * Create new instance for type based SPI.
     *
     * @param type  SPI type
     * @param props SPI properties
     * @return SPI instance
     */
    public final T loadService(final String type, final Properties props) {
        Collection<T> typeBasedServices = loadServices(type);
        if (typeBasedServices.isEmpty()) {
            throw new IllegalStateException(String.format("Invalid `%s` SPI type `%s`.", classType.getName(), type));
        }
        T result = typeBasedServices.iterator().next();
        result.init(props);
        return result;
    }

    public final Map<String,T> loadServiceMap(final Properties props){
        return SPIServiceLoader.loadServiceInstances(classType).stream()
                .peek(s-> s.init(props))
                .collect(Collectors.toMap(TypeBasedSPI::getType, s->s));
    }

    /**
     * Create new service by default SPI type.
     *
     * @return type based SPI instance
     */
    public final T loadService() {
        T result = loadFirstTypeBasedService();
        result.init(new Properties());
        return result;
    }

    private Collection<T> loadServices(final String type) {
        return SPIServiceLoader.loadServiceInstances(classType).stream()
                .filter(input -> type.equalsIgnoreCase(input.getType()))
                .collect(Collectors.toList());
    }

    private T loadFirstTypeBasedService() {
        Collection<T> instances = SPIServiceLoader.loadServiceInstances(classType);
        if (instances.isEmpty()) {
            throw new IllegalStateException(String.format("Invalid `%s` SPI, no implementation class load from SPI.", classType.getName()));
        }
        return instances.iterator().next();
    }
}