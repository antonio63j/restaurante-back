/**
 * 
 */
package com.afl.restaurante.services.files;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author antonio
 *
 */
public interface IUploadFileService {

	Resource cargar (String nombreImagen) throws MalformedURLException; 
	Resource salidaFichero (Path path) throws MalformedURLException;
	// String copia (MultipartFile archivo) throws IOException;
	void copia (Path path, MultipartFile archivo, String nombreArchivo) throws IOException;
	// boolean eliminar (String archivo);
	boolean eliminar(String directorio, String archivo);
	Path getPath(String path, String archivo);
	
}
