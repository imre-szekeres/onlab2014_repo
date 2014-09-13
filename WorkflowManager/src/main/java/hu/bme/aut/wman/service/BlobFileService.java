/**
 * ActionService.java
 */
package hu.bme.aut.wman.service;

import hu.bme.aut.wman.model.BlobFile;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 * @author Gergely Várkonyi
 */
@Stateless
@LocalBean
public class BlobFileService {

	@PersistenceContext
	EntityManager em;

	public void create(BlobFile file) {
		em.persist(file);
	}

	public void update(BlobFile file) {
		em.merge(file);
	}

	public void remove(BlobFile file) {
		em.remove(file);
	}

	public List<BlobFile> findAll() {
		return em.createNamedQuery("BlobFile.findAll", BlobFile.class).getResultList();
	}

	public BlobFile findById(Long id) {
		try {
			TypedQuery<BlobFile> select = em.createNamedQuery("BlobFile.findById", BlobFile.class);
			select.setParameter("id", id);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public BlobFile findByFileName(String fileName) {
		try {
			TypedQuery<BlobFile> select = em.createNamedQuery("BlobFile.findByFileName", BlobFile.class);
			select.setParameter("fileName", fileName);
			return select.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
}
