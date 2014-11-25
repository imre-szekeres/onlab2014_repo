package hu.bme.aut.wman.view.objects;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileUploadVO {

	private CommonsMultipartFile file;

	public FileUploadVO(){
	}

	public CommonsMultipartFile getFile() {
		return file;
	}

	public void setFile(CommonsMultipartFile file) {
		this.file = file;
	}

}