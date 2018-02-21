/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.dsl.dependencies;

import com.google.common.collect.ComparisonChain;
import org.gradle.api.artifacts.ModuleIdentifier;

import java.util.Comparator;
import java.util.Set;

public interface CapabilityInternal {
    Comparator<ModuleIdentifier> MODULE_IDENTIFIER_COMPARATOR = new Comparator<ModuleIdentifier>() {
        @Override
        public int compare(ModuleIdentifier o1, ModuleIdentifier o2) {
            return ComparisonChain.start()
                .compare(o1.getGroup(), o2.getGroup())
                .compare(o1.getName(), o2.getName())
                .result();
        }
    };

    String getCapabilityId();
    Set<ModuleIdentifier> getProvidedBy();
    ModuleIdentifier getPrefer();
    String getReason();
}
