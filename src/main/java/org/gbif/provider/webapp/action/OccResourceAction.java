package org.gbif.provider.webapp.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.appfuse.service.GenericManager;
import org.gbif.provider.datasource.DatasourceInterceptor;
import org.gbif.provider.model.DwcExtension;
import org.gbif.provider.model.OccurrenceResource;
import org.gbif.provider.model.ViewMapping;
import org.gbif.provider.service.DatasourceInspectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;

public class OccResourceAction extends BaseResourceAction implements Preparable{
    private GenericManager<DwcExtension, Long> dwcExtensionManager;
    private GenericManager<ViewMapping, Long> viewMappingManager;
    private List occResources;
    private List<DwcExtension> extensions;
    private OccurrenceResource occResource;
    private Long id;

	
	public void setDwcExtensionManager(
			GenericManager<DwcExtension, Long> dwcExtensionManager) {
		this.dwcExtensionManager = dwcExtensionManager;
	}

	public void setViewMappingManager(
			GenericManager<ViewMapping, Long> viewMappingManager) {
		this.viewMappingManager = viewMappingManager;
	}
	
	public List getOccResources() {
        return occResources;
    }

	public List getExtensions() {
		return extensions;
	}

	public void setId(Long id) {
        this. id =  id;
    }

	public OccurrenceResource getOccResource() {
        return occResource;
    }
    public void setOccResource(OccurrenceResource occResource) {
        this.occResource = occResource;
    }

	public void prepare() {
    	if (getResourceId() != null && !isNew()){
        	occResource = occResourceManager.get(getResourceId());
        }else{
        	occResource = new OccurrenceResource();
        }
    }

	public String execute(){
    	extensions = dwcExtensionManager.getAll();
    	// filter already mapped extensions
    	for (ViewMapping map : occResource.getMappings()){
			extensions.remove(map.getExtension());
    	}
    	return SUCCESS;
    }

    public String list() {
        occResources = occResourceManager.getAll();
        return SUCCESS;
    }

    public String edit() {
        return SUCCESS;
    }
    
    public String save() throws Exception {
        if (cancel != null) {
            return "cancel";
        }
        if (delete != null) {
            return delete();
        }
        
        boolean isNew = (occResource.getId() == null);
        occResource = occResourceManager.save(occResource);
        String key = (isNew) ? "occResource.added" : "occResource.updated";
        saveMessage(getText(key));
        // set new current resource in session
    	session.put(DatasourceInterceptor.SESSION_ATTRIBUTE, occResource.getId());
        return SUCCESS;
    }
    
    public String delete() {
        occResourceManager.remove(occResource.getId());
        saveMessage(getText("occResource.deleted"));
        // remove resource from session
    	session.put(DatasourceInterceptor.SESSION_ATTRIBUTE, null);
        return "delete";
    }

    
}