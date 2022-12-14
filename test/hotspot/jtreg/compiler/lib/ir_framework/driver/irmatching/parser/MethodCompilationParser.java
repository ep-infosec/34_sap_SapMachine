/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package compiler.lib.ir_framework.driver.irmatching.parser;

import compiler.lib.ir_framework.driver.irmatching.Matchable;
import compiler.lib.ir_framework.driver.irmatching.NonIRTestClass;
import compiler.lib.ir_framework.driver.irmatching.irmethod.IRMethod;

import java.util.Map;

/**
 * Class to parse the ideal compile phase and PrintOptoAssembly outputs of the test class and store them into a
 * collection of dedicated IRMethod objects used throughout IR matching.
 *
 * @see IRMethod
 */
public class MethodCompilationParser {
    private final Class<?> testClass;

    public MethodCompilationParser(Class<?> testClass) {
        this.testClass = testClass;
    }

    /**
     * Parse the IR encoding and hotspot_pid* file to create a collection of {@link IRMethod} objects.
     * Return a default/empty TestClass object if there are no applicable @IR rules in any method of the test class.
     */
    public Matchable parse(String hotspotPidFileName, String irEncoding) {
        IREncodingParser irEncodingParser = new IREncodingParser(testClass);
        Map<String, TestMethod> testMethodMap = irEncodingParser.parse(irEncoding);
        if (!testMethodMap.isEmpty()) {
            HotSpotPidFileParser hotSpotPidFileParser = new HotSpotPidFileParser(testClass.getName(), testMethodMap);
            return hotSpotPidFileParser.parse(hotspotPidFileName);
        }
        return new NonIRTestClass();
    }
}
