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

### Entidades y Diagrama

Lista de entidades principales y sus relaciones:

![image](https://github.com/user-attachments/assets/a2226ccf-4078-4077-b7f1-fc11e4e1f04f)


- **Juego** → Relación N:M con **Usuario**
- **Usuario** → Relación 1:N con **Recompensa**
- **Usuario** → Relación 1:N con **Apuesta**
- **Juego** → Relación 1:N con **Apuesta**

### Permisos de los Usuarios

| Tipo de Usuario | Permisos |
|----------------|----------|
| **Administrador** | Gestión completa de todas las entidades |
| **Usuario Registrado** | Creación de sus propios datos y modificación de estos mediante los resultados de las apuestas |
| **Invitado** | Acceso solo a información pública. No se puede jugar sin registrarse |

### Imágenes

| Entidad | Asociadas |
|---------|----------|
| **Juego** | 1 imagen por registro |
| **Recompensa** | 1 imágenen por registro |
| **Apuesta** | Sin imágenes |

## Diversificación del desarrollo
Disclaimer: todos los integrantes hemos participado activamente en el desarrollo de la entrega de la practica, y es muy difícil
resumir todo en 5 commits. Muchos commits se han hecho varias veces para arreglar un pequeño error o optimizar algo. Cabe destacar
que todos hemos participado activamente en la solucion de errores de todo el codigo, por ello 5 commits se hace demasiado corto
como para englobar el trabajo individual de cada participante.
# Andrés
[1. Reforma del backend](https://github.com/DWS-2025/project-grupo-4/commit/50d87590183f5885da2915bf7eeab873558ea761) Reforma de la estructura de la aplicación debido a un mal diseño
[2. DTOs y Mappers corregidos](https://github.com/DWS-2025/project-grupo-4/commit/10afaba9b00ea3636c356ee09307921107fbdc38) Elemento importante para garantizar la securización de la información y el manejo de entidades
[3. Relación N:M entre Juego y User](https://github.com/DWS-2025/project-grupo-4/commit/bba1574aad89160db23cb977f4139965aaf2d23e) Se establece la relación N:M de nuestro proyecto
[4. Implementación de la búsqueda dinámica](https://github.com/DWS-2025/project-grupo-4/commit/f781b186390b185e081cc4da499648b70cf003a7) Establece una de las funcionalidades extras para la entrega 2
[5. Login y Logout](https://github.com/DWS-2025/project-grupo-4/commit/bdf73844db0a6fe94a1cc97d0905e84c456044a5) Establece sesiones de usuarios con Usuario y Contraseña, además de registros y un par de restricciones sencillas para evitar errores, como que no puede haber dos usuarios con nombre repetido
# Adrián
[4de9f96548f6d873f38a6f597ab46d31cfe4c576](1) Solucion de errores en apartado de recompensas, para que la paginacion funcione y el mostrado de premios, a la vez que los gets funcionen tambien
[b4644b64acdfa83a3e77960e5495df6833cc92a3](2) Solucion de errores haciendo el get en /api/Prizes
[d36cd265258c2dbaf9b7c60982c5c8cae7494478](3) Implementacion de BetsAPI en su primera version
[21bdba2820d68135b3eccfc1d7524406887c72b9](4) Implementacion de GamesAPI en su primera version
[85c9062a6015c843229a92684fbdb40bdef1aea8](5) Implementacion de PrizesAPI en su primera version

# Saúl
[d9cd015](1) Desarrollo de todas las API en paralelo a la implementación de los DTO para asegurar una transmisión de entidades segura. 

[24029b6](2) Primer paso en el desarrollo de los DTO, creación de las clases DTO y sus respectivos Mappers con mapStruct.

[dd130e1](3) Creación de la API de sesión (API de usuario).

[8bc9592](4) Implementación de la tabla de relación entre GAME y USER, representando una relación N:M.

[92f71a4](4) Este commit permitió representar las imagenes en bases de datos utilizando MultipartFile y Blob.


## Instalación y Ejecución

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

