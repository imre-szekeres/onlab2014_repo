package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.BlobFile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class BlobFileService extends AbstractDataService<BlobFile> {

	@PostConstruct
	private void init() {
		this.setClass(BlobFile.class);
	}

	public List<BlobFile> findByFileName(String fileName) {
		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>("fileName", fileName));
		return findByParameters(parameterList);
	}
}
