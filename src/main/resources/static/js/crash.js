const multiplierDisplay = document.getElementById('multiplier');
const statusDisplay = document.getElementById('status');
const betInput = document.getElementById('bet');
const placeBetButton = document.getElementById('placeBet');
const cashOutButton = document.getElementById('cashOut');
const historyList = document.getElementById('historyList');
const plane = document.getElementById('plane');
const balanceDisplay = document.getElementById('balance');

let multiplier = 1.0;
let crashPoint = 0;
let isBetPlaced = false;
let betAmount = 0;
let balance = 1000;
let interval;

// Start a new round
function startRound() {
    multiplier = 1.0;
    crashPoint = Math.random() * 5 + 1; // Random crash between 1.00x to 6.00x
    isBetPlaced = false;
    statusDisplay.textContent = 'Waiting for players to place bets...';
    cashOutButton.disabled = true;
    plane.style.left = '0';

    setTimeout(() => {
        statusDisplay.textContent = 'Game started! Cash out before it crashes!';
        startMultiplier();
    }, 3000);
}

// Increment multiplier and animate plane
function startMultiplier() {
    interval = setInterval(() => {
        if (multiplier >= crashPoint) {
            clearInterval(interval);
            statusDisplay.textContent = `Crashed at ${multiplier.toFixed(2)}x!`;
            if (isBetPlaced) {
                statusDisplay.textContent += ' You lost your bet.';
                addToHistory(multiplier.toFixed(2), false);
            }
            resetGame();
        } else {
            multiplier += 0.1;
            multiplierDisplay.textContent = `${multiplier.toFixed(2)}x`;
            plane.style.left = `${(multiplier - 1) * 20}%`; // Move plane
        }
    }, 100);
}

// Handle placing a bet
placeBetButton.addEventListener('click', () => {
    betAmount = parseFloat(betInput.value);
    if (isNaN(betAmount) || betAmount <= 0 || betAmount > balance) {
        alert('Enter a valid bet amount within your balance!');
        return;
    }
    isBetPlaced = true;
    balance -= betAmount;
    balanceDisplay.textContent = balance.toFixed(2);
    placeBetButton.disabled = true;
    cashOutButton.disabled = false;
    statusDisplay.textContent = 'Bet placed! Waiting to cash out...';
});

// Handle cashing out
cashOutButton.addEventListener('click', () => {
    if (isBetPlaced) {
        clearInterval(interval);
        const winnings = (betAmount * multiplier).toFixed(2);
        balance += parseFloat(winnings);
        balanceDisplay.textContent = balance.toFixed(2);
        statusDisplay.textContent = `You cashed out at ${multiplier.toFixed(2)}x and won $${winnings}!`;
        addToHistory(multiplier.toFixed(2), true, winnings);
        resetGame();
    }
});

// Add a result to history
function addToHistory(multiplier, success, winnings = 0) {
    const li = document.createElement('li');
    li.textContent = success
        ? `Cashed out at ${multiplier}x - Won $${winnings}`
        : `Crashed at ${multiplier}x - Lost bet`;
    li.style.color = success ? 'green' : 'red';
    historyList.prepend(li);
}

// Reset for next round
function resetGame() {
    betInput.value = '';
    placeBetButton.disabled = false;
    cashOutButton.disabled = true;
    setTimeout(startRound, 3000);
}

// Start first round
startRound();
