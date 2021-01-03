



## DEPLOY

### Ubuntu 20.04

- instalar openjdk8

- instalar postgresql (version 12)

- generar el jar del proyecto y copiar en /home/antonio/www/aflcv-back/app.jar

- en /home/antonio/www/aflcv-back, crear directorio uploads/imagenes/ y cargar las imagenes necesarias

- en /home/antonio/www/aflcv-back, crear directorio downloads, necesario para que la app coloque el cv para su descarga

- generar el servicio systemclt, para ello creamos el archivo etc/systemd/system/aflcv-service.service:

```
  [Unit]
  Description=Java aflcv Service
  
  [Service]
  User=antonio
  
  WorkingDirectory=/home/antonio/www/aflcv-back
  ExecStart=java -jar /home/antonio/www/aflcv-back/app.jar
  
  [Install]
  WantedBy=multi-user.target
```

  para arrancar el servicio:

```
  sudo systemctl enable aflcv-service
  sudo systemctl start aflcv-service
```

  para el arranque con el boot (en el arranque):

```
  sudo systemctl deamon-reload
```

  

## Certificados para web

  Instalamos con apt snap certbot, en ubuntu 20.04 ya biene instalado openssl y git

```
`sudo snap install certbot --classic`

```

la instalacion de certbot no será visible con 

```
sudo apt list --installed
```

para ver lo instaldo con snap, tendremos que hacer 

```
snap list
```

Para renobar los certificados sería:

```
sudo certbot --config-dir /home/antonio/config-dir renew
```

Para ver el estado de los certificados:

```
sudo certbot --config-dir /home/antonio/config-dir certificates
Saving debug log to /var/log/letsencrypt/letsencrypt.log

- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

Found the following certs:
  Certificate Name: fernandezlucena.es
    Serial Number: 3da014eee84924d3359b8f2b66a893ec99a
    Domains: *.fernandezlucena.es
    Expiry Date: 2020-11-25 19:09:58+00:00 (VALID: 33 days)
    Certificate Path: /home/antonio/config-dir/live/fernandezlucena.es/fullchain.pem
    Private Key Path: /home/antonio/config-dir/live/fernandezlucena.es/privkey.pem
```

Con estos certificados, establecer en el registro tipo "TXT" con nombre "_acme-challenge" y dominio "fernandezlucena" el valor:

"CXSiyrZ3bHpWgipqYGD1d1NcMXIzGiuK411WSgpVt4E"

![](C:\Proyectos\AngularSpring\curriculum2\aflcv-back\configuracionDNS.jpg)

## Emails

### Instalar postfix y dovecot

Para dovecot, solo se ha instalado imap

### Adaptar ficheros de configuracion:

​	/etc/postfix/main.cf
​	/etc/dovecot/conf.d/10-auth.conf
​	/etc/dovecot/conf.d/10-mail.conf
​	/etc/dovecot/conf.d/10-ssl.conf
​	/etc/dovecot/dovecot.conf

### Abrir puertos:

​	ports 25 (SMTP) ok, 587 (SMTP over TLS) ok, 465 (SMTPS) ok, 143 (IMAP) ok, 993 (IMAPS) ok, 110 (POP3) ok, 995 (POP3S) ok

### ver estado del servicio postfix:

```
sudo systemctl -l status postfix@-
```


​          que nos muestra las instancias creadas por postfix

### enviar correo de prueba

```
echo "Subject:Correo Maildir 1" | sendmail info@fernandezlucena.es
```

### ver log:

```
sudo tail -f /var/log/mail.log
```

### ver los correos del usurario info:

```
mail -f /home/info/Maildir
```

### ver protocolos instalados por dovecot:

```
sudo cat /usr/share/dovecot/protocols.d/*.protocol
```



- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

## Persistencia

Lo primero instalar postgresql, después es necesario crear un usuario admin y crear la base de datos aflcv



### JPA

**ManyToMany con @JoinTable**

Las entidades son proyecto y herramienta. En la entidad proyecto se define la tabla join así:

```
	@ManyToMany
	@JoinTable(
			  name = "proyecto_herramienta", 
			  joinColumns = @JoinColumn(name = "proyecto_id"), 
			  inverseJoinColumns = @JoinColumn(name = "herramienta_id"),
			  uniqueConstraints = @UniqueConstraint(columnNames={"proyecto_id", "herramienta_id"})
			  )

	private List<Herramienta> herramientas;
```

Definiendo clave única con el par "proyecto_id" y "herramienta_id, eliminamos el error de duplicidad en dicha tabla, que podría producirse con una actualización del tipo: 

       {
            "nombre": "nombre44",
            "empresa": "ahorro corporación",
            "cliente": "ahorro corporación",
            "inicio": "1999-08-01",
            "fin": "2011-06-30",
            "sectorCliente": "Servicios financieros",
            "descripcion": "experiencias",
            "herramientas": [
        		{"id": "4"},
        		{"id": "4"}
            ]     
        }


La clase Herramienta se ha definido así:

    @JsonIgnore
    @ManyToMany (mappedBy = "herramientas")
    private List<Proyecto> proyectos;


Definidas las entidades de esta manera:

- No permite eliminar un herramienta si está presente en la tabla proyecto-herramienta, es decir, si es utilizada en algún proyecto.

- La eliminación de un proyecto, no elimina elimina ninguna herramienta, aún en el caso de que una herramienta esté siendo utilizada en un único proyecto.

- En una actulización de proyecto, no es posible actulizar los campos de las Herramienta que utiliza dicho proyecto. En la actulización de las herramientas de un proyecto, spring solo tendrá en cuenta la lista de "Id´s" o claves de herramientas. En una única actualización de proyecto (PUT), deberán indicarse todas las herramientas del proyecto:

         {
              "nombre": "nombre44",
              "empresa": "ahorro corporación",
              "cliente": "ahorro corporación",
              "inicio": "1999-08-01",
              "fin": "2011-06-30",
              "sectorCliente": "Servicios financieros",
              "descripcion": "experiencias",
              "herramientas": [
          		{"id": "3"},
          		{"id": "4"}
              ]
          }


