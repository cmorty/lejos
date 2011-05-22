package org.lejos.nxt.ldt.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;

public class PrefsResolver
{
	private IPreferencesService service;
	private IScopeContext[] contexts;
	private String id;
	
	public PrefsResolver(String id, IProject project)
	{
		this.id = id;
		if (project != null)
			this.contexts = new IScopeContext[] {new ProjectScope(project)};
	}
	
	public String getString(String key, String def)
	{
		return service.getString(id, key, def, contexts);
	}
	
	public int getInt(String key, int def)
	{
		return service.getInt(id, key, def, contexts);
	}
	
	public boolean getBoolean(String key, boolean def)
	{
		return service.getBoolean(id, key, def, contexts);
	}
}
