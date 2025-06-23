package com.casino.grupo4_dws.casinoweb.managers;


import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.GameMapper;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import com.casino.grupo4_dws.casinoweb.model.Game;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.repos.GameRepository;
import com.casino.grupo4_dws.casinoweb.repos.UserRepository;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.BlobProxy;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameManager {

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private GameMapper gameMapper;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepo;

    public void deleteGame(long id) {
        if (!gameRepo.existsById(id)) {
            throw new IllegalArgumentException("El juego introducido no existe");
        }
        Game game = gameRepo.findById(id).get();
        for (User user : game.getUsersLiked()) {
            user.getGamesLiked().remove(game);
            userManager.save(user);
        }
//        List<Bet> bets = betMapper.toEntityList(betManager.findAll());
//        for (Bet bet : bets) {
//            bet.setGame(null);
//            betManager.save(bet);
//        }
        gameRepo.deleteById(id);
    }

    public Optional<GameDTO> getGameById(int id) {
        return gameRepo.findGameById(id)
                .map(game -> gameMapper.toDTO(game));
    }

    public GameDTO getGame(int id) {
        Game game = gameRepo.findGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("El juego introducido no existe"));
        return gameMapper.toDTO(game);
    }

    @PostConstruct
    public void postConstruct() throws IOException, SQLException {
        if (!gameRepo.findAll().isEmpty()) {
            return; // Games already initialized
        }
        Game diceGame = new Game();
        diceGame.setTitle("Dado");
        diceGame.setDescription("Tira un dado de seis caras y prueba tu suerte!");
        diceGame.setChance(16);
        diceGame.setMinInput(1);
        diceGame.setMultiplier(6);
        saveGame(diceGame, new MockMultipartFile("file", "Dados.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/dice.jpg"))));

        Game rouletteGame = new Game();
        rouletteGame.setTitle("Ruleta");
        rouletteGame.setDescription("Apuesta a tu color favorito y gira la ruleta!");
        rouletteGame.setChance(50);
        rouletteGame.setMinInput(1);
        rouletteGame.setMultiplier(2);
        saveGame(rouletteGame, new MockMultipartFile("file", "Ssegura.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/Ssegura.jpg"))));

        Game slotsGame = new Game();
        slotsGame.setTitle("Tragaperras");
        slotsGame.setDescription("Las monedas en el bolsillo no te generan mas dinero... aquí si");
        slotsGame.setChance(5);
        slotsGame.setMinInput(10);
        slotsGame.setMultiplier(20);
        saveGame(slotsGame, new MockMultipartFile("file", "Slots.jpg", "image/jpeg", Files.readAllBytes(Paths.get("src/main/resources/static/images/slots.jpg"))));
    }

    public List<GameDTO> getGameList() {
        return gameRepo.findAll().stream()
                .map(game -> gameMapper.toDTO(game))
                .collect(Collectors.toList());
    }

    public List<GameDTO> getGameListMostLiked() {
        List<Game> listaJuego = new ArrayList<>(gameRepo.findAll());
        listaJuego.sort((g1, g2) -> {
            int size1 = g1.getUsersLiked() != null ? g1.getUsersLiked().size() : 0;
            int size2 = g2.getUsersLiked() != null ? g2.getUsersLiked().size() : 0;
            return Integer.compare(size2, size1);
        });
        return gameMapper.toDTOList(listaJuego);
    }

    public GameDTO saveGame(Game noSanitizado, MultipartFile imageFile) throws IOException {

        Game game = sanitize(noSanitizado);

        if (imageFile != null && !imageFile.isEmpty()) {
            game.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        if (game.getChance() < 1 || game.getChance() > 100) {
            throw new IllegalArgumentException("Esta creacion de juego es ilegal");
        }
        Game savedGame = gameRepo.save(game);
        return gameMapper.toDTO(savedGame);
    }

    public GameDTO updateGameDetails(GameDTO updatedGameDTO, int id, MultipartFile imageFile) throws IOException {
        Game game = gameRepo.findGameById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        Game updatedGame = gameMapper.toEntity(updatedGameDTO);

        game.setTitle(updatedGame.getTitle());
        game.setDescription(updatedGame.getDescription());
        game.setMinInput(updatedGame.getMinInput());
        game.setMultiplier(updatedGame.getMultiplier());
        game.setChance(updatedGame.getChance());

        if (imageFile != null && !imageFile.isEmpty()) {
            game.setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
        }

        gameRepo.save(game);
        return gameMapper.toDTO(game);
    }


    public boolean isLiked(GameDTO gamedto, UserDTO userdto) {
        Optional<Game> gameOp = gameRepo.findGameById(gameMapper.toEntity(gamedto).getId());
        if (gameOp.isEmpty()) {
            throw new IllegalArgumentException("El juego introducido no existe");
        }
        Game game = gameOp.get();
        Optional<User> userOp = userRepo.getONEUserById(userMapper.toEntity(userdto).getId());
        if (userOp.isEmpty()) {
            throw new IllegalArgumentException("El user introducido no existe");
        }
        User user = userOp.get();

        if (user.getGamesLiked() == null || user.getGamesLiked().isEmpty()) {
            return false;
        }
        if (game.getUsersLiked() == null || game.getUsersLiked().isEmpty()) {
            return false;
        }
        if (user.getGamesLiked().contains(game) && game.getUsersLiked().contains(user)) {
            return true;
        } else {
            return false;
        }
    }

    public Game sanitize(Game game) {
        game.setDescription(Jsoup.clean(game.getDescription(), Whitelist.relaxed()));
        return game;
    }
}
