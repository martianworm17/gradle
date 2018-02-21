import org.gradle.api.artifacts.dsl.ComponentMetadataHandler

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

subprojects {
    dependencies {
        components {
            // Gradle distribution - minify: remove unused transitive dependencies
            withModule(library("maven3")) {
                allVariants {
                    withDependencies {
                        removeAll {
                            it.name != "maven-settings-builder" &&
                                it.name != "maven-model" &&
                                it.name != "maven-model-builder" &&
                                it.name != "maven-artifact" &&
                                it.name != "maven-aether-provider" &&
                                it.group != "org.sonatype.aether"
                        }
                    }
                }
            }
            withModule(library("awsS3_core")) {
                allVariants {
                    withDependencies { removeAll { it.name == "jackson-dataformat-cbor" } }
                }
            }
            withModule(library("jgit")) {
                allVariants {
                    withDependencies { removeAll { it.group == "com.googlecode.javaewah" }}
                }
            }
            withModule(library("maven3_wagon_http_shared4")) {
                allVariants {
                    withDependencies { removeAll { it.group == "org.jsoup" }}
                }
            }
            withModule(library("aether_connector")) {
                allVariants {
                    withDependencies { removeAll { it.group == "org.sonatype.sisu" } }
                }
            }
            withModule(library("maven3_compat")) {
                allVariants {
                    withDependencies { removeAll { it.group == "org.sonatype.sisu" } }
                }
            }
            withModule(library("maven3_plugin_api")) {
                allVariants {
                    withDependencies { removeAll { it.group == "org.sonatype.sisu" } }
                }
            }

            // Gradle distribution - replace similar library with different coordinates
            replaceGoogleCollectionsWithGuava("org.codehaus.plexus:plexus-container-default")

            replaceJunitDepWithJunit("org.jmock:jmock-junit4")

            replaceBeanshellWithApacheBeanshell("org.testng:testng")

            replaceLog4JWithAdapter("org.apache.xbean:xbean-reflect")

            replaceJCLWithAdapter("org.apache.xbean:xbean-reflect")
            replaceJCLWithAdapter("org.apache.httpcomponents:httpclient")
            replaceJCLWithAdapter("com.amazonaws:aws-java-sdk-core")
            replaceJCLWithAdapter("org.apache.httpcomponents:httpmime")
            replaceJCLWithAdapter("net.sourceforge.htmlunit:htmlunit")
            replaceJCLWithAdapter("org.apache.maven.wagon:wagon-http")
            replaceJCLWithAdapter("org.apache.maven.wagon:wagon-http-shared4")

            replaceJCLConstraintWithAdapter("org.codehaus.groovy:groovy-all")

            replaceAsmWithOW2Asm("com.google.code.findbugs:findbugs")
            replaceAsmWithOW2Asm("org.parboiled:parboiled-java")

            //TODO check if we can upgrade the following dependencies and remove the rules
            downgradeIvy("org.codehaus.groovy:groovy-all")
            downgradeTestNG("org.codehaus.groovy:groovy-all")

            downgradeXmlApis("jaxen:jaxen")
            downgradeXmlApis("jdom:jdom")
            downgradeXmlApis("xalan:xalan")
            downgradeXmlApis("jaxen:jaxen")
        }
    }
}

fun ComponentMetadataHandler.replaceJCLWithAdapter(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                removeAll { it.group == "commons-logging" }
                add ("org.slf4j:jcl-over-slf4j:1.7.10") {
                    because("We do not want non-slf4j logging implementations on the classpath")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceJCLConstraintWithAdapter(module: String) {
    withModule(module) {
        allVariants {
            withDependencyConstraints {
                removeAll { it.group == "commons-logging" }
                add ("org.slf4j:jcl-over-slf4j:1.7.10") {
                    because("We do not want non-slf4j logging implementations on the classpath")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceLog4JWithAdapter(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                removeAll { it.group == "log4j" }
                add ("org.slf4j:log4j-over-slf4j:1.7.16") {
                    because("We do not want non-slf4j logging implementations on the classpath")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceGoogleCollectionsWithGuava(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                removeAll { it.group == "com.google.collections" }
                add("com.google.guava:guava-jdk5:17.0") {
                    because("Guava replaces google collections")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceJunitDepWithJunit(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                removeAll { it.group == "junit" }
                add("junit:junit:4.12") {
                    because("junit:junit replaced junit:junit-dep")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceAsmWithOW2Asm(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                filter { it.group == "asm" }.forEach {
                    add("org.ow2.asm:${it.name}")
                }
                removeAll { it.group == "asm" }
            }
        }
    }
}

fun ComponentMetadataHandler.replaceBeanshellWithApacheBeanshell(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                removeAll { it.group == "org.beanshell" }
                add("org.apache-extras.beanshell:bsh:2.0b6") {
                    because("org.apache-extras.beanshell:bsh replaced junit:junit-dep")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.downgradeIvy(module: String) {
    withModule(module) {
        allVariants {
            withDependencyConstraints {
                filter { it.group == "org.apache.ivy" }.forEach {
                    it.version { prefer("2.2.0") }
                    it.because("Gradle depends on ivy implementation details which changed with newer versions")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.downgradeTestNG(module: String) {
    withModule(module) {
        allVariants {
            withDependencyConstraints {
                filter { it.group == "org.testng" }.forEach {
                    it.version { prefer("6.3.1") }
                    it.because("6.3.1 is required by Gradle and part of the distribution")
                }
            }
        }
    }
}

fun ComponentMetadataHandler.downgradeXmlApis(module: String) {
    withModule(module) {
        allVariants {
            withDependencies {
                filter { it.group == "xml-apis" }.forEach {
                    it.version { prefer("1.4.01") }
                    it.because("Gradle has trouble with the versioning scheme and pom redirects in higher versions")
                }
            }
        }
    }
}
