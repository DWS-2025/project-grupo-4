document.addEventListener('DOMContentLoaded', function() {
    const gridContainer = document.getElementById('grid-container');

    // Función para cargar y mostrar la lista de premios
    function loadPrizes() {
        fetch('/api/Prizes')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar los premios');
                }
                return response.json();
            })
            .then(prizes => {
                gridContainer.innerHTML = ''; // Limpiar el contenedor antes de añadir los juegos
                prizes.forEach(prize => {
                    // Crear un <section> para cada premio
                    const prizeSection = document.createElement('section');
                    prizeSection.classList.add('game-section');

                    // Crear el título <h2> con el nombre del juego
                    const prizeTitle = document.createElement('h2');
                    prizeTitle.textContent = prize.title;

                    const prizeImg = document.createElement('img');
                    prizeImg.src = prize.image;

                    // Crear el párrafo <p> con la descripción del juego
                    const prizeDescription = document.createElement('p');
                    prizeDescription.textContent = prize.description;

                    // Añadir el título y la descripción al <section>
                    prizeSection.appendChild(prizeTitle);
                    prizeSection.appendChild(prizeDescription);
                    prizeSection.appendChild(prizeImg);

                    // Añadir el <section> al contenedor de la cuadrícula
                    gridContainer.appendChild(prizeSection);
                });
            })
            .catch(error => {
                console.error('Error al cargar los premios:', error);
                gridContainer.innerHTML = '<p>Error al cargar los premios Por favor, inténtalo de nuevo más tarde.</p>';
            });
    }

    // Cargar los premios al cargar la página
    loadPrizes();
});