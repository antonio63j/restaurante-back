package com.afl.restaurante.services.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.afl.restaurante.utilpdf.UtilPdf;

@Service
public class DownloadFileService implements IDownloadFileService {
	@Autowired 
	private UtilPdf utilPdf;
	
	//public final static String DIRECTORIO_DOWNLOAD = "downloads/";	
	public final static String PREFIJO_FILE_DOWNLOAD = "cv-AntonioFernandezLucena.pdf";	
	
	@Value("${app.downloadsDir:downloads/}")
	private String downloadsDir;
	
	//public final static String FILE_DONWLOAD = "donwloads";	

	@Override
	public String getFilenameGenerado(String cursos, String herramientas, String proyectos) throws Exception{

	    String filename = downloadsDir + PREFIJO_FILE_DOWNLOAD;

		utilPdf.generarPdfCv(filename, cursos, herramientas, proyectos);
		
		//document.close();
		return PREFIJO_FILE_DOWNLOAD;
	}

	@Override
	public boolean deleteFilename(String archivo) {
		if (archivo != null && archivo.length() > 0) {
			Path pathRuta = getPath (downloadsDir, archivo);
			File archivoToDelete = pathRuta.toFile();
			if (archivoToDelete.exists() && archivoToDelete.canRead()) {
				archivoToDelete.delete();
				return true;
			}
		}
		return false;
	}
	
	public Path getPath(String path, String archivo) {
		Path rutaArchivo = Paths.get(path).resolve(archivo).toAbsolutePath();
		return rutaArchivo;
	}

}
