package com.afl.restaurante.services.files;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IDownloadFileService {
	
	//String getFilenameGenerado () throws FileNotFoundException, IOException;
	String getFilenameGenerado (String cursos, String herramientas, String proyectos) throws Exception;
	boolean deleteFilename (String filename);

}
