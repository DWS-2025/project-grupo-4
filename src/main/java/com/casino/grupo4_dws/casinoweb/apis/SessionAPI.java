
package com.casino.grupo4_dws.casinoweb.apis;

import com.casino.grupo4_dws.casinoweb.dto.GameDTO;
import com.casino.grupo4_dws.casinoweb.dto.PrizeDTO;
import com.casino.grupo4_dws.casinoweb.managers.GameManager;
import com.casino.grupo4_dws.casinoweb.managers.JWTManager;
import com.casino.grupo4_dws.casinoweb.managers.PrizeManager;
import com.casino.grupo4_dws.casinoweb.managers.UserManager;
import com.casino.grupo4_dws.casinoweb.mapper.PrizeMapper;
import com.casino.grupo4_dws.casinoweb.model.User;
import com.casino.grupo4_dws.casinoweb.dto.UserDTO;
import com.casino.grupo4_dws.casinoweb.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class SessionAPI {

    @Autowired
    private UserManager userManager;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private GameManager gameManager;
    @Autowired
    private PrizeManager prizeManager;
    @Autowired
    private JWTManager jwtManager;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userManager.getUserList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        if (userManager.findById(id).isPresent()) {
            return ResponseEntity.ok(userManager.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@RequestHeader("Authorization") String jwtToken, @RequestBody Map<String, Object> data) {
        if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
            try {
                String username = (String) data.get("username");
                String password = (String) data.get("password");
                Integer money = (Integer) data.get("money");
                Boolean isAdmin = (Boolean) data.get("isAdmin");

                User user = new User();
                user.setUserName(username);
                user.setPassword(userManager.hashPassword(password));
                user.setMoney(money);
                user.setIsadmin(isAdmin);

                userManager.save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(user));
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestHeader("Authorization") String jwtToken, @RequestBody Map<String, Object> data) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), id)) {
            try {
                User user = userManager.findUserById(id).get();
                String username = (String) data.get("username");
                String password = (String) data.get("password");
                Integer money = (Integer) data.get("money");
                Boolean isAdmin = (Boolean) data.get("isAdmin");


                if (username != null) user.setUserName(username);
                if (password != null) user.setPassword(userManager.hashPassword(password));

                if (jwtManager.tokenBelongsToAdmin(jwtManager.extractTokenFromHeader(jwtToken))) {
                    if (money != null) user.setMoney(money);
                    if (isAdmin != null) user.setIsadmin(isAdmin);
                }
                UserDTO userDTO = userMapper.toDTO(user);
                userManager.updateUser(userDTO, id);
                return ResponseEntity.ok(userDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@RequestHeader("Authorization") String jwtToken, @PathVariable int id) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), id)) {
            if (userManager.findById(id).isPresent()) {
                userManager.deleteUser(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{idUser}/favorite")
    public ResponseEntity<List<GameDTO>> getFavoriteGames(@PathVariable int idUser) {
        if (userManager.findById(idUser).isPresent()) {
            UserDTO userDTO = userManager.findById(idUser).get();
            List<GameDTO> favList = userManager.getFavGames(userDTO);
            return ResponseEntity.ok(favList);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> addFavorite(@RequestHeader("Authorization") String jwtToken, @PathVariable int idUser, @PathVariable int idGame) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), idUser)) {
            if (userManager.findById(idUser).isPresent() && gameManager.getGameById(idGame).isPresent()) {
                userManager.setFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
                return ResponseEntity.ok(userManager.findById(idUser).get());
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{idUser}/favorite/{idGame}")
    public ResponseEntity<UserDTO> removeFavorite(@RequestHeader("Authorization") String jwtToken, @PathVariable int idUser, @PathVariable int idGame) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), idUser)) {
            if (userManager.findById(idUser).isPresent() && gameManager.getGameById(idGame).isPresent()) {
                userManager.deleteFav(userManager.findById(idUser).get(), gameManager.getGameById(idGame).get());
                return ResponseEntity.ok(userManager.findById(idUser).get());
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{idUser}/buy/{idPrize}")
    public ResponseEntity<UserDTO> buy(@RequestHeader("Authorization") String jwtToken, @PathVariable int idUser, @PathVariable int idPrize) {
        if (jwtManager.tokenHasPermission(jwtManager.extractTokenFromHeader(jwtToken), idUser)) {
            Optional<UserDTO> userDTO = userManager.findById(idUser);
            Optional<PrizeDTO> prizeDTO = prizeManager.findById(idPrize);

            if (userDTO.isPresent() && prizeDTO.isPresent()) {
                userManager.buyPrize(prizeDTO.get(), userDTO.get());
                UserDTO updatedUser = userManager.findById(idUser).get();
                return ResponseEntity.ok(updatedUser);
            } else
                return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /*
    Methods Register and Login accept two user & pass strings because
    UserDTO does not contain a password field, so using a DTO would make
    data transfer harder, and also allow user to modify their params (money, isAdmin...)
     */

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        return ResponseEntity.status(HttpStatus.CREATED).body(userManager.saveUser(username, password));

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");

        if (userManager.isUserCorrect(username, password)) {
            UserDTO userDTO = userMapper.toDTO(userManager.findByUsername(username).get());
            return ResponseEntity.ok(jwtManager.generateToken(userDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Only for testing purposes, delete before final release
    @GetMapping("/testtoken")
    public ResponseEntity<Claims> testToken(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.replace("Bearer ", "");
        return ResponseEntity.ok(jwtManager.verifyToken(token));
    }
    @PostMapping("/{id}/document")
    public ResponseEntity<String> uploadDocument(@RequestHeader("Authorization") String jwtToken,
                                                 @PathVariable("id") int userId,
                                                 @RequestParam("document") MultipartFile document) {
        String token = jwtManager.extractTokenFromHeader(jwtToken);

        if (!jwtManager.tokenHasPermission(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para subir documentos para este usuario.");
        }

        try {
            userManager.saveUserDocument(userId, document);
            return ResponseEntity.ok("Documento subido con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir el documento: " + e.getMessage());
        }
    }
    @GetMapping("/{id}/document")
    public ResponseEntity<byte[]> viewDocument(@RequestHeader("Authorization") String jwtToken,
                                               @PathVariable("id") int userId) {
        String token = jwtManager.extractTokenFromHeader(jwtToken);

        if (!jwtManager.tokenHasPermission(token, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            String documentPath = userManager.getUserDocumentPath(userId);
            File file = new File(documentPath);

            if (!file.exists() || !file.canRead()) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());

            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
