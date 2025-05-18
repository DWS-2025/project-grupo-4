# Casino La Miel ✨💵

## Integrantes del Equipo de Desarrollo 📝

| Nombre | Apellidos | Correo Universitario | GitHub |
|--------|----------|----------------------|--------|
| Andrés | Alonso Pérez | a.alonsop.2023@alumnos.urjc.es | [gig4ndr3s](https://github.com/gig4ndr3s) |
| Adrián | Camacho Martín | a.camacho.2023@alumnos.urjc.es | [RalpiRekt](https://github.com/RalpiRekt) |
| Saúl | Torres Jiménez | s.torres.2023@alumnos.urjc.es | [saultj](https://github.com/saultj) |

## Descripción de la Aplicación Web ✍️

Se trata de una página web de un casino digital donde puedes jugar diferentes apuestas en varios juegos y, partiendo de un dinero inicial, podrás llegar a comprar diferentes recompensas dependiendo de tu suerte.

## Aspectos Principales

### Entidades y Diagrama 🏛️

Lista de entidades principales y sus relaciones:

![image](https://github.com/user-attachments/assets/a2226ccf-4078-4077-b7f1-fc11e4e1f04f)


- **Juego** → Relación N:M con **Usuario**
- **Usuario** → Relación 1:N con **Recompensa**
- **Usuario** → Relación 1:N con **Apuesta**
- **Juego** → Relación 1:N con **Apuesta**

### Permisos de los Usuarios 🧍

| Tipo de Usuario | Permisos |
|----------------|----------|
| **Administrador** | Gestión completa de todas las entidades |
| **Usuario Registrado** | Creación de sus propios datos y modificación de estos mediante los resultados de las apuestas |
| **Invitado** | Acceso solo a información pública. No se puede jugar sin registrarse |

### Imágenes 🖼️

| Entidad | Asociadas |
|---------|----------|
| **Juego** | 1 imagen por registro |
| **Recompensa** | 1 imágenen por registro |
| **Apuesta** | Sin imágenes |

## Diversificación del desarrollo ⌨️
Disclaimer: todos los integrantes hemos participado activamente en el desarrollo de la entrega de la practica, y es muy difícil
resumir todo en 5 commits. Muchos commits se han hecho varias veces para arreglar un pequeño error o optimizar algo. Cabe destacar
que todos hemos participado activamente en la solucion de errores de todo el codigo, por ello 5 commits se hace demasiado corto
como para englobar el trabajo individual de cada participante.
# Andrés: Top 5 commits entrega 2
[1. Reforma del backend](https://github.com/DWS-2025/project-grupo-4/commit/50d87590183f5885da2915bf7eeab873558ea761) Reforma de la estructura de la aplicación debido a un mal diseño. 

[2. DTOs y Mappers corregidos](https://github.com/DWS-2025/project-grupo-4/commit/10afaba9b00ea3636c356ee09307921107fbdc38) Elemento importante para garantizar la securización de la información y el manejo de entidades. 

[3. Relación N:M entre Juego y User](https://github.com/DWS-2025/project-grupo-4/commit/bba1574aad89160db23cb977f4139965aaf2d23e) Se establece la relación N:M de nuestro proyecto. 

[4. Implementación de la búsqueda dinámica](https://github.com/DWS-2025/project-grupo-4/commit/f781b186390b185e081cc4da499648b70cf003a7) Establece una de las funcionalidades extras para la entrega 2. 

[5. Login y Logout](https://github.com/DWS-2025/project-grupo-4/commit/bdf73844db0a6fe94a1cc97d0905e84c456044a5) Establece sesiones de usuarios con Usuario y Contraseña, además de registros y un par de restricciones sencillas para evitar errores, como que no puede haber dos usuarios con nombre repetido. 

# Andrés: Top 5 commits entrega 3
[1. Tokens CSRF](https://github.com/DWS-2025/project-grupo-4/commit/ab18c1d622f7e80f6131268eb12e754be03206a0) Añadidos tokens anti-CSRF en los formularios gestionados por los controladores GET para los POST

[2. Implementación de texto enriquecido](https://github.com/DWS-2025/project-grupo-4/commit/6bcc7a8ee6c4a4af6df1042639a0b8c4b6f24e82) Introducido texto enriquecido para las descripciones de los juegos. Se sanitiza con una librería al insertar en la base de datos.

[3. HTTPS](https://github.com/DWS-2025/project-grupo-4/commit/2247338892e36bbc931e1bcecbcf6a5459860551) Integrado el certificado autofirmado para establecer una seguridad HTTPS, esencial para cifrar datos.

[4. Arreglo búsqueda dinámica](https://github.com/DWS-2025/project-grupo-4/commit/8128521737235bea012e9305f3ebfdf7b9992418) Funcionalidad esencial y necesaria de la entrega anterior que no funcionaba debido a que chocaba el script de la paginación contra la llamada dinámica que debía ser también a la API.

[5. Modificar Info de usuario](https://github.com/DWS-2025/project-grupo-4/commit/dc634939511bd2460575fc3414f4d7f54fc0748d) Funcionalidad para que un usuario logeado pueda modificar su contraseña y su usuario, que más adelante sirvió para el edit user del admin. En un principio este commit tenía errores por cómo actualizaba la información y cómo mostraba la información, pero fueron arreglados en actualizaciones posteriores.

# Adrián entrega 3
https://github.com/DWS-2025/project-grupo-4/commit/1e92b0bcf1087e564ab43c9f23a2705cecd93a2b (1) Subida de fichero pdf completamente funcional
https://github.com/DWS-2025/project-grupo-4/commit/608c2407b4676d3806eaffe96a9931997cdb350c (2) Admin panel funcional para gestionar usuarios
https://github.com/DWS-2025/project-grupo-4/commit/e953425ee30253e0bc2673e9288ab542bab92baa (3) Version final de GamesAPI y PrizesAPI
https://github.com/DWS-2025/project-grupo-4/commit/f18597611099773882efb05d129591e9be582098 (4) Version beta de admin panel
https://github.com/DWS-2025/project-grupo-4/commit/7885488f16a28ae2b6d8015d377f9992287807fa (5) PreAlpha de admin panel

# Saúl entrega 3
https://github.com/DWS-2025/project-grupo-4/commit/c121cd1321efaf783dfec70d53632e8f8a527963 (1) Mejoras de manejo y seguridad en el servicio para gestionar guardado de usuarios.
https://github.com/DWS-2025/project-grupo-4/commit/188834307a302067cb97d6ab434eb49075d19bdd (2) Los propios usuarios (y admins) pueden modificar sus recursos desde la API.
https://github.com/DWS-2025/project-grupo-4/commit/b6ac170c22555c369ea2ef1a44ee34138e70a9e9 (3) Uso de BCript para hashear las passwords en la BBDD. 
https://github.com/DWS-2025/project-grupo-4/commit/80409282099a22dca6a1a68d5dad9e18bfc66887 (4) Acceso a API securizada por JWT.
https://github.com/DWS-2025/project-grupo-4/commit/594adaaac3e45cb2116013021fafa1e23379b10e (5) Gestión de tokens JWT.

# Saúl entrega 2
[d9cd015](1) Desarrollo de todas las API en paralelo a la implementación de los DTO para asegurar una transmisión de entidades segura. 

[24029b6](2) Primer paso en el desarrollo de los DTO, creación de las clases DTO y sus respectivos Mappers con mapStruct.

[dd130e1](3) Creación de la API de sesión (API de usuario).

[8bc9592](4) Implementación de la tabla de relación entre GAME y USER, representando una relación N:M.

[92f71a4](4) Este commit permitió representar las imagenes en bases de datos utilizando MultipartFile y Blob.


## Instalación y Ejecución 💻

```sh
# Clonar el repositorio
git clone https://github.com/DWS-2025/project-grupo-4.git

# Navegar al directorio del proyecto
cd repo

# Instalar dependencias
npm install

# Ejecutar la aplicación
npm start
```

