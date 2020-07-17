package com.worldNavigator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

public class Game {
    public PlayerViewer playerViewer;
    public List<PlayerViewer> playerViewers = new ArrayList<>();
    public List<String> playersSessions = new ArrayList<>();
    private Maps maps;
    private int map_index = 0;
    public boolean isStarted = false;
    final DB db;
    int id;

    public Game(int id) {
        this.db = new DB();
        this.db.getCollection("Maps").drop();
        this.db.generateCollection("Maps");
        this.db.getCollection("Players").drop();
        this.db.generateCollection("Players");
        this.db.getCollection("Rooms").drop();
        this.db.generateCollection("Rooms");
        this.db.getCollection("Walls").drop();
        this.db.generateCollection("Walls");
        this.db.getCollection("Keys").drop();
        this.db.generateCollection("Keys");
        this.db.getCollection("Doors").drop();
        this.db.generateCollection("Doors");
        this.db.getCollection("Chests").drop();
        this.db.generateCollection("Chests");
        this.db.getCollection("Paintings").drop();
        this.db.generateCollection("Paintings");
        this.db.getCollection("Mirrors").drop();
        this.db.generateCollection("Mirrors");
        this.db.getCollection("Sellers").drop();
        this.db.generateCollection("Sellers");
        this.db.getCollection("MasterKeys").drop();
        this.db.generateCollection("MasterKeys");
        this.db.getCollection("Safes").drop();
        this.db.generateCollection("Safes");
        this.db.getCollection("Windows").drop();
        this.db.generateCollection("Windows");
        this.db.getCollection("Tables").drop();
        this.db.generateCollection("Tables");
        this.db.getCollection("Gates").drop();
        this.db.generateCollection("Gates");

        this.id = id;
        Maps maps = new Maps(this);
        maps.addMap("map.json");
        setMaps(maps, "0");
    }

    public void setMaps(Maps maps) {
        this.maps = maps;
        this.map_chooser();
    }

    public void setMaps(Maps maps, String sc) {
        this.maps = maps;
        this.map_chooser(sc);
    }

    public void map_chooser() {
        System.out.println("Choose one of the available maps: ");
        int counter = 0;
        while (counter != this.maps.maps.size()) {
            System.out.println(counter + ": " + this.maps.maps.get(counter));
            ++counter;
        }
        System.out.println("Enter map number");
        Scanner sc = new Scanner(System.in);
        this.map_index = sc.nextInt();
    }

    public void map_chooser(String sc) {
        this.map_index = Integer.parseInt(sc);
    }

    public boolean isPlayerExisted(String sessionId) throws IOException {
        for (String id : this.playersSessions) {
            if (id != null) {
                if (id.equals(sessionId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void preparePlayer(String name, String playerSession) {
        playersSessions.add(playerSession);
        PlayerController player = new PlayerController(new PlayerModel(this.maps.maps.get(this.map_index), this, name, playerSession));
        this.playerViewer = new PlayerViewer(player, name, playerSession);
        playerViewers.add(this.playerViewer);
        player.playerModel.addToRoom(this.playerViewer);
    }

    public void start() throws IOException {
        if (this.playerViewers.size() == 2) {
            this.isStarted = true;
            for (PlayerViewer user : this.playerViewers) {
                if (user != null) {
                    user.playerController.startGame();
                }
            }
        }
    }

    private String exit(HttpServletRequest request) {
        if (!this.isStarted) {
            return "Game didn't start yet, " + this.playerViewers.size() + " players available, game must have " + 2 + " players";
        }
        PlayerViewer user = null;
        System.out.println("request.getParameter(\"sessionId\"): " + request.getParameter("sessionId"));
        for (PlayerViewer playerViewer : playerViewers) {
            if (playerViewer.playerSession.equals(request.getParameter("sessionId"))) {
                user = playerViewer;
            }
        }
        assert user != null;
        System.out.println("user: " + user.getName());
        if (user.playerController.playerModel.isPlaying()) {
            String message = "";
            if (request.getParameter("quit") != null) {
                this.removePlayer(user);
            }
        }
        return "You quited the game";
    }

    public String playerCommand(HttpServletRequest request) {
        if (!this.isStarted) {
            return "Game didn't start yet, " + this.playerViewers.size() + " players available, game must have " + 2 + " players";
        }

        if (this.playerViewers.size() == 1) {
            return "<b>" + "You are the last player, You won!" + "</b>";
        }
        PlayerViewer user = null;
        for (PlayerViewer playerViewer : playerViewers) {
            if (playerViewer.playerSession.equals(request.getParameter("sessionId"))) {
                user = playerViewer;
            }
        }
        assert user != null;
        if (user.playerController.playerModel.isPlaying()) {
            String message = "";
            if (request.getParameter("left") != null) {
                message = "l";
            } else if (request.getParameter("forward") != null) {
                message = "f";
            } else if (request.getParameter("right") != null) {
                message = "r";
            } else if (request.getParameter("backward") != null) {
                message = "b";
            } else if (request.getParameter("check") != null) {
                message = "c";
            } else if (request.getParameter("key") != null) {
                message = "key";
            } else if (request.getParameter("open") != null) {
                message = "open";
            } else if (request.getParameter("look") != null) {
                message = "look";
            } else if (request.getParameter("room") != null) {
                message = "room";
            } else if (request.getParameter("light") != null) {
                message = "light";
            } else if (request.getParameter("flash") != null) {
                message = "flash";
            } else if (request.getParameter("location") != null) {
                message = "location";
            } else if (request.getParameter("items") != null) {
                message = "items";
            } else if (request.getParameter("quit") != null) {
                this.exit(request);
            }
            user.playerController.use_method(message.trim());
            return user.msg;
        }
        return "";
    }

    public PlayerViewer whoIsIt_name(String name) {
        PlayerViewer user = null;
        for (PlayerViewer playerViewer : playerViewers) {
            if (playerViewer.getName().equals(name)) {
                user = playerViewer;
            }
        }
        return user;
    }

    public PlayerViewer whoIsIt(HttpServletRequest request) {
        PlayerViewer user = null;
        for (PlayerViewer playerViewer : playerViewers) {
            if (playerViewer.playerSession.equals(request.getParameter("sessionId"))) {
                user = playerViewer;
            }
        }
        return user;
    }

    public void fight(PlayerViewer playerViewer1, PlayerViewer playerViewer2) {
        int power1 = playerViewer1.playerController.playerModel.power();
        int power2 = playerViewer2.playerController.playerModel.power();
        if (power1 > power2) {
            this.removePlayer(playerViewer2, playerViewer1);
        } else if (power1 < power2) {
            this.removePlayer(playerViewer1, playerViewer2);
        } else {
            playerViewer1.playerController.setIsFighting(true);
            playerViewer1.playerController.setFightingAgainst(playerViewer2);
            playerViewer2.playerController.setIsFighting(true);
            playerViewer2.playerController.setFightingAgainst(playerViewer1);
        }
    }

    public boolean isFighting(PlayerViewer playerViewer) {
        if (playerViewer == null) return false;
        return playerViewer.playerController.isFighting();
    }

    public void setFightingChoice(HttpServletRequest request, String choice) {
        this.whoIsIt(request).playerController.setFightingChoice(choice);
        if (this.whoIsIt(request).playerController.fightingAgainst().playerController.getFightingChoice() != null) {

            PlayerViewer lostPlayerViewer = this.whoLost(this.whoIsIt(request), this.whoIsIt(request).playerController.fightingAgainst());
            PlayerViewer wonPlayerViewer = this.whoWon(this.whoIsIt(request), this.whoIsIt(request).playerController.fightingAgainst());

            wonPlayerViewer.playerController.setFightingAgainst(null);
            wonPlayerViewer.playerController.setIsFighting(false);
            wonPlayerViewer.playerController.setFightingChoice(null);

            this.removePlayer(lostPlayerViewer, wonPlayerViewer);

            if (playerViewers.size() == 1) {
                playerViewers.get(0).playerController.setWinner();
            }

        }
    }

    public PlayerViewer whoLost(PlayerViewer playerViewer1, PlayerViewer playerViewer2) {
        switch (playerViewer1.playerController.getFightingChoice()) {
            case "rock":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return null;
                    case "paper":
                        return playerViewer1;
                    case "scissor":
                        return playerViewer2;
                }
                break;
            case "paper":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return playerViewer2;
                    case "paper":
                        return null;
                    case "scissor":
                        return playerViewer1;
                }
                break;
            case "scissor":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return playerViewer1;
                    case "paper":
                        return playerViewer2;
                    case "scissor":
                        return null;
                }
                break;
            default:
                return null;
        }
        return null;
    }

    public PlayerViewer whoWon(PlayerViewer playerViewer1, PlayerViewer playerViewer2) {
        switch (playerViewer1.playerController.getFightingChoice()) {
            case "rock":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return null;
                    case "paper":
                        return playerViewer2;
                    case "scissor":
                        return playerViewer1;
                }
                break;
            case "paper":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return playerViewer1;
                    case "paper":
                        return null;
                    case "scissor":
                        return playerViewer2;
                }
                break;
            case "scissor":
                switch (playerViewer2.playerController.getFightingChoice()) {
                    case "rock":
                        return playerViewer2;
                    case "paper":
                        return playerViewer1;
                    case "scissor":
                        return null;
                }
                break;
            default:
                return null;
        }
        return null;
    }

    public void removePlayer(PlayerViewer playerViewer, PlayerViewer winner) {
        int freeGolds = playerViewer.playerController.getGolds();
        int freeFlashLights = playerViewer.playerController.getFlashLights();
        ArrayList<KeyChecker> freeKeys = playerViewer.playerController.getKeys();
        this.playerViewers.remove(playerViewer);
        this.playersSessions.remove(playerViewer.playerSession);
        this.distributeGolds(freeGolds);
        winner.playerController.addFlashLights(freeFlashLights);
        winner.playerController.addKeys(freeKeys);
    }

    public void removePlayer(PlayerViewer playerViewer) {
        System.out.println("playerViewer: " + playerViewer.getName());
        int freeGolds = playerViewer.playerController.getGolds();
        int freeFlashLights = playerViewer.playerController.getFlashLights();
        ArrayList<KeyChecker> freeKeys = playerViewer.playerController.getKeys();
        System.out.println("this.playerViewer.playerController.playerModel.roomIndex: " + playerViewer.playerController.playerModel.roomIndex);
        Map<String, Object> emptyLocation = playerViewer.playerController.playerModel.map.rooms.get(playerViewer.playerController.playerModel.roomIndex).getEmptyLocation();

        JSONObject content = new JSONObject();
        JSONArray keys = new JSONArray();
        System.out.println("((Wall) emptyLocation.get(\"wall\")).items : " + ((Wall) emptyLocation.get("wall")).items);
        for (Key key : (ArrayList<Key>) playerViewer.playerController.playerModel.contents.get("keys")) {
            System.out.println("key: " + key);
            keys.add(key);
        }
        content.put("keys", keys);
        content.put("flashLights", playerViewer.playerController.playerModel.contents.get("flashLights"));

        JSONObject json = new JSONObject();
        json.put("location", emptyLocation.get("location"));
        json.put("content", content);

        System.out.println("emptyLocation: " + emptyLocation);
        System.out.println(json);
        ((Wall) emptyLocation.get("wall")).itemsFactory.preparePainting(json, true);
        this.playerViewers.remove(playerViewer);
        this.playersSessions.remove(playerViewer.playerSession);
        this.distributeGolds(freeGolds);
    }

    public void distributeGolds(int freeGolds) {
        int goldsDivision = freeGolds / this.playerViewers.size();
        for (PlayerViewer playerViewer : this.playerViewers) {
            playerViewer.playerController.addGolds(goldsDivision);
        }
    }

    public void endGame(PlayerViewer winner) {
        for (PlayerViewer playerViewer : this.playerViewers) {
            if (playerViewer.playerSession != winner.playerSession) {
                this.removePlayer(playerViewer);
            }
        }
    }

    public void quit() {
        System.exit(1);
    }
}
