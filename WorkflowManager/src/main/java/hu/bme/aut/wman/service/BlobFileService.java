package hu.bme.aut.wman.service;

import hu.bme.aut.wman.exceptions.EntityNotFoundException;
import hu.bme.aut.wman.model.BlobFile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Helps make operations with <code>BlobFile</code>.
 * 
 * @version "%I%, %G%"
 */
@Stateless
@LocalBean
public class BlobFileService extends AbstractDataService<BlobFile> {

	public List<BlobFile> selectByFileName(String fileName) throws EntityNotFoundException {
		List<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(BlobFile.PR_FILE_NAME, fileName));
		return selectByOwnProperties(parameterList);
	}

	@Override
	protected Class<BlobFile> getEntityClass() {
		return BlobFile.class;
	}
}
