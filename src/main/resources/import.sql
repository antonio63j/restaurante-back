INSERT INTO usuarios (username, password, finalizada_activacion, enabled, nombre, apellidos, email) VALUES ('antonio63j@hotmail.com','12345', true, true, 'Antonio','fernandez','antonio63j@hotmail.com');

INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (1, 1);
INSERT INTO usuarios_roles (usuario_id, role_id) VALUES (1, 2);

INSERT INTO adminindex ( cabecera, imagen, body, route) VALUES ('configuración de la web', 'empresa.png', 'En este apartado se podrá cambiar los datos de contacto de la empresa', 'empresa' )
INSERT INTO adminindex ( cabecera, imagen, body, route) VALUES ('images de la página principal', 'home.png', 'En este apartado se podrá cambiar los imagenes de la página de entrada (home)', 'admslider' )
INSERT INTO adminindex ( cabecera, imagen, body, route) VALUES ('clasificación de platos', 'tipos.png', 'En este apartado se podrá clasificar los ditintos platos, por ejemplo añadir tipo carnes, pescados, ...', 'admtipoplato')

INSERT INTO empresa(descripcion_breve, direccion, email, horario, nombre, provincia, telefono, urlweb) VALUES ('descripcion breve', 'c/Cortubi n.22', '@info@fernandezlucena.es', 'horario de 9 a 22h','Cazuela Cortubí', 'Madrid', '627336511', 'http://localhost:4200')

INSERT INTO sliders ( img_file_name, label, descripcion) VALUES ('slider1.jpg', 'labelx adsllla lllllaaa kkkkkfinal', 'descripcionx')
INSERT INTO sliders ( img_file_name, label, descripcion) VALUES ('slider2.jpg', 'labelx lll jaajajajaja aaa y final', 'descripcionx')
INSERT INTO sliders ( img_file_name, label, descripcion) VALUES ('slider3.jpg', 'labelx nnnnnnnnnnnnnnnnnnnnnnnnnnn', 'descripcion')
