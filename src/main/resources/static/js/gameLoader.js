document.addEventListener('DOMContentLoaded', function() {
    const gridContainer = document.getElementById('grid-container');

    // Función para cargar y mostrar la lista de juegos
    function loadGames() {
        fetch('/api/Games')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar los juegos');
                }
                return response.json();
            })
            .then(games => {
                gridContainer.innerHTML = ''; // Limpiar el contenedor antes de añadir los juegos
                games.forEach(game => {
                    // Crear un <section> para cada juego
                    const gameSection = document.createElement('section');
                    gameSection.classList.add('game-section');

                    // Crear el título <h2> con el nombre del juego
                    const gameTitle = document.createElement('h2');
                    gameTitle.textContent = game.title;

                    const gameImg = document.createElement('img');
                    gameImg.src = game.image;

                    // Crear el párrafo <p> con la descripción del juego
                    const gameDescription = document.createElement('p');
                    gameDescription.textContent = game.description;

                    // Añadir el título y la descripción al <section>
                    gameSection.appendChild(gameTitle);
                    gameSection.appendChild(gameDescription);
                    gameSection.appendChild(gameImg);

                    // Añadir el <section> al contenedor de la cuadrícula
                    gridContainer.appendChild(gameSection);
                });
            })
            .catch(error => {
                console.error('Error al cargar los juegos:', error);
                gridContainer.innerHTML = '<p>Error al cargar los juegos. Por favor, inténtalo de nuevo más tarde.</p>';
            });
    }

    // Cargar los juegos al cargar la página
    loadGames();
});