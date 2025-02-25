document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/Prizes")
        .then(response => response.json())
        .then(data => {
            const gridContainer = document.getElementById("grid-container");
            data.forEach(prize => {
                const prizeSection = document.createElement("section");
                prizeSection.className = "game-section";

                prizeSection.innerHTML = `
                    <h2>${prize.title}</h2>
                    <img src="${prize.image}" alt="${prize.title}" width="190" height="190">
                    <p>${prize.description}</p>
                    <button role="button" class="golden-button" onclick="anadirAlcarrito('${prize.title}')">
                        <span class="golden-text">Añadir al carro</span>
                    </button>
                `;

                gridContainer.appendChild(prizeSection);
            });
        })
        .catch(error => {
            console.error("Error al cargar los premios:", error);
        });
});