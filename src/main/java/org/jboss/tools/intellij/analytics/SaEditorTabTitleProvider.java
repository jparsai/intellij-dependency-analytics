/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.fileEditor.impl.EditorTabTitleProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;


public class SaEditorTabTitleProvider implements EditorTabTitleProvider{
    @Override
    public @NotNull String getEditorTabTitle(@NotNull Project project, @NotNull VirtualFile file) {
        // Check if file opened in Editor is SA report, if Yes then change the title of Custom Editor Tab
        // This won't rename the physical file, hence physical temp file will still remain unique.
        if ("sa".equals(file.getExtension()))
            return "Dependency Analytics Report";
        return file.getName();
    }
}
