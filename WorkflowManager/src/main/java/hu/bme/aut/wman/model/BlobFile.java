package hu.bme.aut.wman.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation to store files in database
 *
 * @version "%I%, %G%"
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "FILE_STORAGE")
public class BlobFile extends AbstractEntity {

	public static final String PR_FILE_NAME = "fileName";
	public static final String PR_STATE = "state";
	public static final String PR_CONTENT = "content";

	@NotNull
	private String fileName;

	@NotNull
	@ManyToOne
	private Project project;

	@Lob
	private byte[] content;

	@Deprecated
	public BlobFile() {
		super();
	}

	public BlobFile(String fileName, byte[] content, Project project) {
		this.fileName = fileName;
		this.content = content;
		this.setProject(project);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getContent() {
		return content;
	}


	public void setContent(byte[] content)
	{
		this.content = content;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BlobFile))
			return false;
		BlobFile other = (BlobFile) object;
		if ((id == null && other.id != null) || (id != null && !id.equals(other.id)))
			return false;
		return true;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
