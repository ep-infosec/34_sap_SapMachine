/*
 * Copyright (c) 2008, 2022, Oracle and/or its affiliates. All rights reserved.
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
 *
 */

package com.sun.hotspot.igv.coordinator.actions;

import com.sun.hotspot.igv.coordinator.FolderNode;
import com.sun.hotspot.igv.data.Folder;
import com.sun.hotspot.igv.data.GraphDocument;
import com.sun.hotspot.igv.data.Group;
import com.sun.hotspot.igv.data.serialization.Printer;
import com.sun.hotspot.igv.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import javax.swing.Action;
import javax.swing.JFileChooser;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Thomas Wuerthinger
 */
public final class SaveAsAction extends NodeAction {

    public SaveAsAction() {
        putValue(Action.SHORT_DESCRIPTION, "Save selected groups to XML file...");
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        GraphDocument doc = new GraphDocument();
        for (Node node : activatedNodes) {
            if (node instanceof FolderNode) {
                FolderNode folderNode = (FolderNode) node;
                Folder folder = folderNode.getFolder();
                if (folder instanceof Group) {
                    Group group = (Group) folder;
                    doc.addElement(group);
                }
            }
        }
        save(doc);
    }

    public static void save(GraphDocument doc) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(ImportAction.getFileFilter());
        fc.setCurrentDirectory(new File(Settings.get().get(Settings.DIRECTORY, Settings.DIRECTORY_DEFAULT)));

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".xml");
            }

            File dir = file;
            if (!dir.isDirectory()) {
                dir = dir.getParentFile();
            }
            Settings.get().put(Settings.DIRECTORY, dir.getAbsolutePath());
            try {
                try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()))) {
                    Printer p = new Printer();
                    p.export(writer, doc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SaveAsAction.class, "CTL_SaveAsAction");
    }

    @Override
    protected String iconResource() {
        return "com/sun/hotspot/igv/coordinator/images/save.png";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes.length > 0) {
            for (Node n : nodes) {
                if (!(n instanceof FolderNode) || ((FolderNode) n).isRootNode()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
