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

### Entidades

Lista de entidades principales y sus relaciones:

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
[]
[]
[]
[]
[]
# Adrián
[4de9f96548f6d873f38a6f597ab46d31cfe4c576] Solucion de errores en apartado de recompensas, para que la paginacion funcione y el mostrado de premios, a la vez que los gets funcionen tambien
[b4644b64acdfa83a3e77960e5495df6833cc92a3] Solucion de errores haciendo el get en /api/Prizes
[d36cd265258c2dbaf9b7c60982c5c8cae7494478] Implementacion de BetsAPI en su primera version
[21bdba2820d68135b3eccfc1d7524406887c72b9] Implementacion de GamesAPI en su primera version
[85c9062a6015c843229a92684fbdb40bdef1aea8] Implementacion de PrizesAPI en su primera version

# Saúl
[d9cd015] Desarrollo de todas las API en paralelo a la implementación de los DTO para asegurar una transmisión de entidades segura. 

[24029b6] Primer paso en el desarrollo de los DTO, creación de las clases DTO y sus respectivos Mappers con mapStruct.

[dd130e1] Creación de la API de sesión (API de usuario).

[8bc9592] Implementación de la tabla de relación entre GAME y USER, representando una relación N:M.

[92f71a4] Este commit permitió representar las imagenes en bases de datos utilizando MultipartFile y Blob.

## Diagrama de la Base de Datos

Se incluirá un diagrama con las entidades de la base de datos, sus campos y relaciones. Puede ser un diagrama entidad-relación o un diagrama UML de las clases Java.
(**En desarrollo**)
![Diagrama de la Base de Datos](ruta/a/diagrama.png)

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

## Licencia

Este proyecto está bajo la licencia [Nombre de la Licencia](https://opensource.org/licenses/tipo).

