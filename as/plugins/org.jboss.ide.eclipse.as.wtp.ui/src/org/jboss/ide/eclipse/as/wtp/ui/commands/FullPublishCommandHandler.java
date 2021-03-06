/*************************************************************************************
 * Copyright (c) 2008-2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.ide.eclipse.as.wtp.ui.commands;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.internal.ui.stringsubstitution.SelectedResourceManager;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.PublishServerJob;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.NullModuleArtifact;
import org.jboss.ide.eclipse.as.wtp.core.util.ServerModelUtilities;

public class FullPublishCommandHandler extends AbstractModuleCommandHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// go around in a circle to avoid plugin dependence on debug.ui
			IResource resource = SelectedResourceManager.getDefault().getSelectedResource();
			IModule module = getModule(resource);
			IServer server = getServer(module, new NullModuleArtifact(module), new NullProgressMonitor());
			if( module != null && server != null ) {
				IModule[] module2 = new IModule[]{module};
				((Server)server).setModulePublishState(module2, IServer.PUBLISH_STATE_FULL);
				ArrayList<IModule[]> allChildren = ServerModelUtilities.getDeepChildren(server, module2);
				for( int j = 0; j < allChildren.size(); j++ ) {
					((Server)server).setModulePublishState((IModule[])allChildren.get(j), IServer.PUBLISH_STATE_FULL);
				}
				new PublishServerJob(server, IServer.PUBLISH_INCREMENTAL, true).schedule();
			}
			return null;
		} catch( CoreException ce) {
			throw new ExecutionException(ce.getMessage(), ce.getStatus().getException());
		}
	}

}
