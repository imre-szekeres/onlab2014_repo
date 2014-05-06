package hu.bme.aut.tomeesample.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FILE_STORAGE")
@NamedQueries(
{
		@NamedQuery(name = "FileStorage.findAll", query = "SELECT f FROM BlobFile f"),
		@NamedQuery(name = "FileStorage.findByFsPk", query = "SELECT f FROM BlobFile f WHERE f.id = :id"),
		@NamedQuery(name = "FileStorage.findByFileName", query = "SELECT f FROM BlobFile f WHERE f.fileName = :fileName")
})
public class BlobFile implements Serializable
{
	private static final long serialVersionUID = -4796720242338042828L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	public Long getId()
	{
		return id;
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
