# Casino La Miel ✨💵

## Integrantes del Equipo de Desarrollo 📝

| Nombre | Apellidos | Correo Universitario | GitHub |
|--------|----------|----------------------|--------|
| Andrés | Alonso Pérez | a.alonsop.2023@alumnos.urjc.es | [gig4ndr3s](https://github.com/gig4ndr3s) |
| Adrián | Camacho Martín | a.camacho.2023@alumnos.urjc.es | [RalpiRekt](https://github.com/RalpiRekt) |
| Saúl | Torres Jiménez | s.torres.2023@alumnos.urjc.es | [saultj](https://github.com/saultj) |

## Descripción de la Aplicación Web ✍️

Se trata de una página web de un casino digital donde puedes jugar diferentes apuestas, y, dependiendo de tu suerte, podrás llegar a comprar diferentes recompensas.

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
[d39f4a85a7be4c27b48107bb3a1759b694ae93ee](1) Se adapta el uso de los DTOs a los controladores de la API Web
[59237575a352932d6e9cf15871f6ea3373f3bcca](2) En este Commit se establece y se hace funcional la relación N:M de nuestro proyecto
[11c8b9b4379bb4c138d41766a1506b24a113dbb3](3) En este Commit se permite el borrado de los juegos a los que ya se ha jugado
[1c1ea20219edfc54334b05dc87a1384fbacf81eb](4) Hace funcional la parte más importante de nuestra página web
[bdf73844db0a6fe94a1cc97d0905e84c456044a5](5) Establece sesiones de usuarios con Usuario y Contraseña, además de registros y un par de restricciones sencillas para evitar errores, como que no puede haber dos usuarios con nombre repetido
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

