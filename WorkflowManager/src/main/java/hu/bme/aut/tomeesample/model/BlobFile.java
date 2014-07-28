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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Table(name = "FILE_STORAGE")
@NamedQueries(
{
		@NamedQuery(name = "FileStorage.findAll", query = "SELECT f FROM BlobFile f"),
		@NamedQuery(name = "FileStorage.findById", query = "SELECT f FROM BlobFile f WHERE f.id = :id"),
		@NamedQuery(name = "FileStorage.findByFileName", query = "SELECT f FROM BlobFile f WHERE f.fileName = :fileName")
})
public class BlobFile implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@NotNull
	private String fileName;

	@Setter
	@NotNull
	@ManyToOne
	private State state;

	@Setter
	@Lob
	private byte[] content;

	public BlobFile(String fileName, byte[] content, State state)
	{
		this.fileName = fileName;
		this.content = content;
		this.state = state;
	}
}
