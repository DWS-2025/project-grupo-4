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

