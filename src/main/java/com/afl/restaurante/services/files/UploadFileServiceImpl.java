package com.afl.restaurante.services.files;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileServiceImpl implements IUploadFileService {
	
	public final static String DIRECTORIO_UPLOAD = "uploads";
	private Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);
	
	@Override
	public Resource salidaFichero(Path path) throws MalformedURLException {
		log.info("salida de fichero: " + path.toString());
		
		Resource resource = null;
		resource = new UrlResource(path.toUri());
	    if(!resource.exists() || !resource.isReadable()) {
	    	Path path2 = getPath("src/main/resources/static/images", "no-photo.png");
			resource = new UrlResource(path2.toUri());
			log.error("Error, no se pudo cargar la imagen " + path.toString() + " se sustituye por imagen anónima");
	    };	
		return resource;
	}
	

//	@Override
//	public String copia(MultipartFile archivo) throws IOException {
//
//		String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", "");
//		Path rutaArchivo = getPath(DIRECTORIO_UPLOAD, nombreArchivo);
//		log.info("upload: " + rutaArchivo.toString());
//		Files.copy(archivo.getInputStream(), rutaArchivo);
//		return nombreArchivo;
//	}
	
	@Override
	public void copia(Path rutaArchivo, MultipartFile archivo, String nombreArchivo) throws IOException {

		log.info("upload: " + rutaArchivo.toString());
		Files.copy(archivo.getInputStream(), rutaArchivo);
	}

//	@Override
//	public boolean eliminar(String archivo) {
//		if (archivo != null && archivo.length() > 0) {
//			Path rutaFotoAnterior = getPath (DIRECTORIO_UPLOAD, archivo);
//			File archivoFotoAnterior = rutaFotoAnterior.toFile();
//			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
//				if (!archivoFotoAnterior.delete()) {
//					log.debug (" Error al eliminar: " + rutaFotoAnterior.toString() );
//					return false;
//				}
//			   else {
//        		 return true;  
//			   }
//			}	
//			else { 	
//			  log.debug (" Error al eliminar fichero, no existe o no se puede leer: " + rutaFotoAnterior.toString() );
//		  	  return false;
//			}
//		}
//		log.debug (" Error al eliminar archivo, nombre de fichero invalido " + archivo);
//		return false;
//	}
	
	@Override
	public boolean eliminar(String directorio, String archivo) {
		if (archivo != null && archivo.length() > 0) {
			Path rutaFotoAnterior = getPath (directorio, archivo);
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				if (!archivoFotoAnterior.delete()) {
					log.debug (" Error al eliminar: " + rutaFotoAnterior.toString() );
					return false;
				}
			   else {
        		 return true;  
			   }
			}	
			else { 	
			  log.debug (" Error al eliminar fichero, no existe o no se puede leer: " + rutaFotoAnterior.toString() );
		  	  return false;
			}
		}
		log.debug (" Error al eliminar archivo, nombre de fichero invalido " + archivo);
		return false;
	}

	@Override
	public Path getPath(String path, String archivo) {
		Path rutaArchivo = Paths.get(path).resolve(archivo).toAbsolutePath();
		return rutaArchivo;
	}
	

}
