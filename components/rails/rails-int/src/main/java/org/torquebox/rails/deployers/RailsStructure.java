/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.torquebox.rails.deployers;

import java.io.IOException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.spi.structure.StructureMetaData;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.vfs.VirtualFile;
import org.torquebox.mc.vdf.AbstractRubyStructureDeployer;
import org.torquebox.rack.deployers.RackStructure;

/**
 * <pre>
 * Stage: structure
 *    In: 
 *   Out: classpath entries and metadata locations
 * </pre>
 * 
 * StructureDeployer to identify Ruby-on-Rails applications.
 * 
 * @author Bob McWhirter
 */
public class RailsStructure extends AbstractRubyStructureDeployer {

    /**
     * Construct.
     */
    public RailsStructure() {
        setRelativeOrder(-1000);
    }

    public boolean doDetermineStructure(StructureContext structureContext) throws DeploymentException {
        VirtualFile root = structureContext.getFile();

        try {
            VirtualFile environment = root.getChild("config/environment.rb");
            if (environment.exists()) {
                StructureMetaData structureMetaData = structureContext.getMetaData();
                ContextInfo context = RackStructure.createRackContextInfo(root, structureMetaData);
                addPluginJars( structureContext, context );
                structureMetaData.addContext(context);
                return true;
            }
        } catch (IOException e) {
            throw new DeploymentException(e);
        }

        return false;
    }

    protected void addPluginJars(StructureContext structureContext, ContextInfo context) throws IOException {
        VirtualFile root = structureContext.getRoot();
        VirtualFile vendorPlugins = root.getChild("vendor/plugins");
        if (vendorPlugins != null) {
            List<VirtualFile> plugins = vendorPlugins.getChildren();

            for (VirtualFile plugin : plugins) {
                VirtualFile pluginLibJava = plugin.getChild("lib");
                addDirectoryOfJarsToClasspath(structureContext, context, pluginLibJava.getPathNameRelativeTo(root));
                List<VirtualFile> jars = vendorPlugins.getChildrenRecursively(JAR_FILTER);
                for (VirtualFile jar : jars) {
                    addClassPath(structureContext, jar, true, true, context);
                }
            }
        }
    }

    @Override
    protected boolean hasValidName(VirtualFile file) {
        return file.getName().endsWith(".rails") || file.getChild("config/environment.rb").exists();
    }

    @Override
    protected boolean hasValidSuffix(String name) {
        return true;
    }

}
