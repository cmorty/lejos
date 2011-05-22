package org.lejos.nxt.ldt.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;

public class PrefsResolver
{
	private IPreferencesService service;
	private IScopeContext[] contexts;
	private String qualifier;
	
	public PrefsResolver(String qualifier, IProject project)
	{
		this.qualifier = qualifier;
		this.service = Platform.getPreferencesService();
		
		if (project != null)
			this.contexts = new IScopeContext[] {new ProjectScope(project)};
	}
	
	public String getString(String key, String def)
	{
		return service.getString(qualifier, key, def, contexts);
	}
	
	public int getInt(String key, int def)
	{
		return service.getInt(qualifier, key, def, contexts);
	}
	
	public boolean getBoolean(String key, boolean def)
	{
		return service.getBoolean(qualifier, key, def, contexts);
	}
}
