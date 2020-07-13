package com.worldNavigator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    public PlayerViewer playerViewer;
    public List<PlayerViewer> playerViewers = new ArrayList<>();
    public List<String> playersSessions = new ArrayList<>();
    private Maps maps;
    private int map_index = 0;
    public boolean isStarted = false;

    public Game() {
        Maps maps = new Maps();
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
        PlayerController player = new PlayerController(new PlayerModel(this.maps.maps.get(this.map_index), this, name));
        this.playerViewer = new PlayerViewer(player, name, playerSession);
        playerViewers.add(this.playerViewer);
        playersSessions.add(playerSession);
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

    public String playerCommand(HttpServletRequest request) {
        if (!this.isStarted) {
            return "Game didn't start yet, " + this.playerViewers.size() + " players available, game must have " + 2 + " players";
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
            this.playersSessions.remove(playerViewer2.playerSession);
            this.playerViewers.remove(playerViewer2);
        } else if (power1 < power2) {
            this.playersSessions.remove(playerViewer1.playerSession);
            this.playerViewers.remove(playerViewer1);
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

            this.removePlayer(lostPlayerViewer);

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
            default: return null;
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
            default: return null;
        }
        return null;
    }

    public void removePlayer(PlayerViewer playerViewer) {
        this.playerViewers.remove(playerViewer);
        this.playersSessions.remove(playerViewer.playerSession);
    }

    public void quit() {
        System.exit(1);
    }
}
