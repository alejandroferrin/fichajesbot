
# FichajesBot [Para Telegram]

## Descripción

**Fichajesbot** es una aplicación diseñada para almacenar el registro de entradas y salidas de los empleados en pequeñas empresas a través de un bot en Telegram. Con Fichajesbot, los empleados pueden fichar fácilmente sus jornadas laborales, lo que permite un seguimiento del tiempo trabajado.

## Características

- **Registro Sencillo**: Los empleados pueden registrar su entrada y salida directamente desde Telegram, haciendo que el proceso sea rápido y accesible.
- **Base de Datos**: Todos los fichajes quedan almacenados en una base de datos segura, garantizando la integridad y disponibilidad de la información.
- **Informes Mensuales**: Los administradores pueden generar informes mensuales de las jornadas laborales de cada empleado, facilitando la gestión del tiempo y la planificación de recursos.
- **Interfaz Amigable**: La integración con Telegram proporciona una experiencia de usuario intuitiva que no requiere conocimientos técnicos previos.

## ¿Cómo Funciona?

1. **Registro en el Bot**: Los empleados inician el fbot de Telegram y configuran su perfil.
2. **Fichar**: Para fichar, solo necesitan enviar un comando específico al bot indicando su entrada o salida.
3. **Almacenamiento**: Los fichajes se almacenan en la base de datos, donde se agrupan por empleado y fecha.
4. **Generación de Informes**: Los administradores pueden solicitar informes mensuales con un simple comando al bot.


## Instalación

Repositorio de instalación: https://github.com/alejandroferrin/fichajesbot_dist

## Requisitos Previos

Antes de proceder con la instalación, asegúrate de tener:

- Un servidor (puede ser un VPS o un servidor en la nube).
- Acceso SSH a dicho servidor.
- Ansible instalado en tu máquina local.

## Instalación de Ansible

Si no tienes Ansible instalado, puedes instalarlo usando pip (si tienes Python) o usando apt en sistemas basados en Debian.

Para instalarlo con apt:

```
sudo apt update
sudo apt install ansible
```

O con pip:

```
pip install ansible
```


## Configuración del Servidor

### Copiar la Clave SSH

Utiliza el siguiente comando para copiar tu clave SSH al servidor. Esto te permitirá conectarte sin necesidad de introducir la contraseña cada vez.

```
ssh-copy-id root@your_server_ip
```


Reemplaza your_server_ip por la IP de tu servidor.

### Configurar el Archivo hosts

Edita el archivo hosts y añade la IP de tu servidor bajo el grupo [new] como se muestra a continuación:

```
[new]
44.111.115.176 ansible_ssh_user=root
```

Ejemplo de conexión con clave .pem (Ejemplo para una clave hipotética /home/alex/fichajesbot/fichajesbot.pem)

```
[new]
11.11.11.111 ansible_user=ubuntu ansible_ssh_private_key_file=/home/alex/fichajesbot/fichajesbot.pem

```



Reemplaza 44.111.115.176 con la dirección IP real de tu servidor.

### Modificar Variables en setup.yml

Antes de la instalación, debes modificar ciertas variables en el archivo setup.yml situado en el repositorio. Asegúrate de ingresar valores adecuados y seguros:

```
vars:
...
db_pass: thisshouldbeastrongpassword # Modificar
mysql_root_password: strongrootpassword # Modificar
telegram_bot_token: bot_token # Modificar
telegram_bot_name: bot_name # Modificar
encryption_key: generated_key # Modificar
admin_default_activation_code: 484948 # Modificar
admin_default_name: John Doe
...
```


### Generación de la Clave de Encriptación

La clave de encriptación debe ser generada utilizando el archivo keyGenerator.html que se encuentra dentro del repositorio. Simplemente ábrelo en un navegador y sigue las instrucciones para generar una clave.

## Comandos de Ansible

Los siguientes comandos, que se deben ejecutar desde la carpeta 'ansible' te permitirán instalar y gestionar la aplicación en tu servidor:

### Probar conexión

```
ansible -i hosts new -m ping
```


### Instalación

Para instalar la aplicación, ejecuta el siguiente comando:

```
ansible-playbook -i hosts setup.yml
```


### Reiniciar el Servidor

Si necesitas reiniciar el servidor, utiliza:

```
ansible-playbook -i hosts reboot.yml
```


### Actualización del Sistema Operativo

Para actualizar el sistema operativo en el servidor, ejecuta:

```
ansible-playbook -i hosts update-so.yml
```

## Uso de Docker

En lugar de usar Ansible, puedes optar por desplegar la aplicación utilizando Docker. Esto puede ser útil si prefieres un enfoque más ágil y flexible, o si deseas levantar la aplicación en una máquina diferente que tenga Docker instalado.

### Configuración de docker-compose.yml

Dentro de la carpeta compose, encontrarás un archivo llamado docker-compose.yml. En este archivo, puedes editar directamente las variables como contraseñas y otros parámetros de configuración según tus necesidades.

```
services:
  db:
    container_name: fichajesbotdb
    image: 'linuxserver/mariadb:10.11.10'
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=fichajesbotrootpass #Cambiar password si se desea
      - MYSQL_DATABASE=db_fichajesbot
      - MYSQL_USER=fichajes #Cambiar usuario si se desea
      - MYSQL_PASSWORD=fichajespass #Cambiar password si se desea
    volumes:
      - db_data:/var/lib/mysql
    restart: always

  app:
    container_name: fichajesbotapp
    image: 'alexfer/fichajesbot:0.1.0'
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/db_fichajesbot
      - SPRING_DATASOURCE_USERNAME=fichajes #Usar mismo usuario que la base de datos
      - SPRING_DATASOURCE_PASSWORD=fichajespass #Usar misma contraseña que la base de datos
      - TELEGRAM_BOT_TOKEN=bot_token
      - TELEGRAM_BOT_NAME=test_fichajes_bot
      - ENCRYPTION_KEY=B4KKHg+zjUMyfRbfOEiIiQ==
      - ADMIN_DEFAULT_ACTIVATION_CODE=45
      - ADMIN_DEFAULT_NAME=John Doe
    ports:
      - "8080:8080"
    links:
      - "db"
    depends_on:
      - "db"
    restart: always
volumes:
  db_data:

```

Modifica los valores según lo que desees y guarda los cambios.

### Levantar la Aplicación

Para levantar la aplicación utilizando Docker Compose, navega hasta la carpeta compose y ejecuta el siguiente comando:

```
docker-compose up -d
```


Esto iniciará todos los contenedores definidos en el archivo docker-compose.yml en modo desatendido.

