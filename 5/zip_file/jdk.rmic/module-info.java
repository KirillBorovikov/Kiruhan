/*
 * Copyright (c) 2014, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/**
 * Defines the <em>{@index rmic rmic}</em> compiler for generating stubs and
 * skeletons using the Java Remote Method Protocol (JRMP) and
 * stubs and tie class files (IIOP protocol) for remote objects.
 *
 * <dl style="font-family:'DejaVu Sans', Arial, Helvetica, sans serif">
 * <dt class="simpleTagLabel">Tool Guides:
 * <dd>{@extLink rmic_tool_reference rmic}
 * </dl>
 *
 * @moduleGraph
 * @since 9
 */
module jdk.rmic {
    requires java.corba;
    requires jdk.compiler;
    requires jdk.javadoc;
}
