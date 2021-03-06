/*******************************************************************************
 * Copyright (c) 2007 - 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.ide.eclipse.as.rse.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.files.IHostFile;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.IDeployableServer;
import org.jboss.ide.eclipse.as.core.server.IJBossServerPublishMethodType;
import org.jboss.ide.eclipse.as.core.server.internal.BehaviourModel;
import org.jboss.ide.eclipse.as.core.util.ServerConverter;
import org.jboss.ide.eclipse.as.rse.core.RSEPublishMethod;
import org.jboss.ide.eclipse.as.rse.core.RSEUtils;
import org.jboss.ide.eclipse.as.ui.IExploreBehavior;

public class RSEExploreBehavior implements IExploreBehavior {
	public void openExplorer(IServer server, IModule[] module) {
		IDeployableServer ds = ServerConverter.getDeployableServer(server);
		String remote = RSEUtils.getDeployRootFolder(ds);
		IPath remoteFolder = new Path(remote == null ? "/" : remote);
		IJBossServerPublishMethodType type = BehaviourModel.getPublishMethodType(server, null);
		RSEPublishMethod method = (RSEPublishMethod)type.createPublishMethod();
		method.setBehaviour(ServerConverter.getDeployableServerBehavior(server));
		if( module != null ) {
			remoteFolder = ds.getDeploymentLocation(module, true);
		}
		try {
			method.getFileService();
			method.ensureConnection(new NullProgressMonitor());
			IHostFile file = method.getFileService().getFile(remoteFolder.removeLastSegments(1).toOSString(), remoteFolder.lastSegment(), new NullProgressMonitor());
			String path = remoteFolder.toString();
			
			IRemoteFile rf = method.getFileServiceSubSystem().getRemoteFileObject(path, null);
			
			SystemShowInTableAction act = new SystemShowInTableAction(Display.getDefault().getActiveShell()); 
			act.setSelectedObject(rf);
			act.run();
		} catch(SystemMessageException e) {
			
		} catch(CoreException ce) {
			
		}
	}

	@Override
	public boolean canExplore(IServer server, IModule[] module) {
		return true;
	}
}
