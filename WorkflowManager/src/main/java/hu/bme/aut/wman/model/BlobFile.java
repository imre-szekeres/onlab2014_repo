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
	private State state;

	@Lob
	private byte[] content;

	public BlobFile()
	{
	}

	public BlobFile(String fileName, byte[] content, State state)
	{
		this.fileName = fileName;
		this.content = content;
		this.state = state;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public byte[] getContent()
	{
		return content;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setContent(byte[] content)
	{
		this.content = content;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof BlobFile))
		{
			return false;
		}
		BlobFile other = (BlobFile) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
		{
			return false;
		}
		return true;
	}
}
