package com.worldNavigator;

import java.io.IOException;
import java.util.*;

public class PlayerController implements PlayerControllerInterface {
    PlayerModel playerModel;
    private final List<String> DOOR_COMMANDS = new ArrayList<>();
    private final List<String> CHEST_COMMANDS = new ArrayList<>();
    private final List<String> MIRROR_COMMANDS = new ArrayList<>();
    private final List<String> PAINTING_COMMANDS = new ArrayList<>();
    private final List<String> SELLER_COMMANDS = new ArrayList<>();
    private boolean hint = true;
    protected Map<String, Command> commandsMap;
    protected Map<String, Command> shortCommandsMap = new HashMap<>();
    public final String HINT_SENTENCE = ", use <hints> command to stop these hints";

    public PlayerController(PlayerModel playerModel) {
        this.playerModel = playerModel;
        this.init_commands();
        this.init_shortCommands();
        String checkString = "check";
        CHEST_COMMANDS.add(checkString);
        MIRROR_COMMANDS.add(checkString);
        PAINTING_COMMANDS.add(checkString);
        String useKeyString = "useKey";
        CHEST_COMMANDS.add(useKeyString);
        DOOR_COMMANDS.add(useKeyString);
        DOOR_COMMANDS.add("open");
        String tradeString = "trade";
        SELLER_COMMANDS.add(tradeString);
    }

    public void subscribe(PlayerViewer playerViewer) {
        this.playerModel.addObserver(playerViewer);
    }

    public void startGame() throws IOException {
        this.playerModel.startGame();
//        while (this.playerModel.isPlaying()) {
//            this.playerModel.notify_player("Enter your next command: ");
//            String command = this.playerModel.br.readLine();
//            if (this.playerModel.isPlaying()) use_method(command.trim());
//        }
    }

    public void use_command(String command) {
        if (this.playerModel.isPlaying()) use_method(command.trim());
    }

    public void init_shortCommands() {
        this.shortCommandsMap.put("o", PlayerControllerInterface::myOrientation);
        this.shortCommandsMap.put("loc", PlayerControllerInterface::myLocation);
        this.shortCommandsMap.put("l", PlayerControllerInterface::rotateLeft);
        this.shortCommandsMap.put("r", PlayerControllerInterface::rotateRight);
        this.shortCommandsMap.put("f", playerController -> move(MoveTypes.forward));
        this.shortCommandsMap.put("b", playerController -> move(MoveTypes.backward));
        this.shortCommandsMap.put(
                "c",
                playerController -> {
                    this.check();
                    this.acquire_items();
                });
        this.shortCommandsMap.put("items", PlayerControllerInterface::myItems);
        this.shortCommandsMap.put("key", PlayerControllerInterface::use_key);
        this.shortCommandsMap.put("light", PlayerControllerInterface::switchLights);
        this.shortCommandsMap.put("flash", PlayerControllerInterface::flashLight);
        this.shortCommandsMap.put("master", playerController -> this.playerModel.use_masterKey());
        this.shortCommandsMap.put("setloc", PlayerControllerInterface::setLocation);
    }

    public void init_commands() {
        Map<String, Command> c = new HashMap<>();

        c.put("room", PlayerControllerInterface::room);
        c.put(
                "orientation",
                playerController -> {
                    this.myOrientation();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <o> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "location",
                playerController -> {
                    this.myLocation();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <loc> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put("wall", PlayerControllerInterface::wall);
        c.put("look", PlayerControllerInterface::look);
        c.put(
                "left",
                playerController -> {
                    this.rotateLeft();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <l> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "right",
                playerController -> {
                    this.rotateRight();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <r> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "forward",
                playerController -> {
                    move(MoveTypes.forward);
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <f> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "backward",
                playerController -> {
                    move(MoveTypes.backward);
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <b> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "check",
                playerController -> {
                    this.check();
                    this.acquire_items();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <c> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "myItems",
                playerController -> {
                    this.myItems();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <items> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "useKey",
                playerController -> {
                    this.use_key();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <key> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put("open", PlayerControllerInterface::open);
        c.put("trade", PlayerControllerInterface::trade);
        c.put(
                "switchLight",
                playerController -> {
                    this.switchLights();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <light> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put(
                "flashLight",
                playerController -> {
                    this.flashLight();
                    if (this.hint)
                        this.playerModel.notify_player(
                                "You can use <flash> as a shortcut command" + this.HINT_SENTENCE);
                });
        c.put("commands", PlayerControllerInterface::commands);
        c.put("time", PlayerControllerInterface::time);
        c.put("hints", PlayerControllerInterface::switchHints);
        c.put("quit", PlayerControllerInterface::quit);

        this.commandsMap = c;
    }

    public void myItems() {
        this.playerModel.myItems();
    }

    public void rotateLeft() {
        this.playerModel.rotateLeft();
    }

    public void rotateRight() {
        this.playerModel.rotateRight();
    }

    public void myLocation() {
        this.playerModel.notify_player(this.playerModel.getLocation());
    }

    public void myOrientation() {
        this.playerModel.notify_player(this.playerModel.getOrientation());
    }

    public void move(MoveTypes move) {
        this.playerModel.move(move);
    }

    public void wall() {
        this.playerModel.wall();
    }

    public void look() {
        this.playerModel.look();
    }

    public void room() {
        this.playerModel.notify_player(this.playerModel.getRoom());
    }

    public void check() {
        this.playerModel.check();
    }

    public String getType() {
        return this.playerModel.getType();
    }

    public void acquire_items() {
        this.playerModel.acquire_items();
    }

    public void use_key() {
        this.playerModel.use_key();
    }

    public void open() {
        this.playerModel.open();
    }

    public void setLocation() {
        this.playerModel.setLocation();
    }

    public void trade() {
        this.playerModel.trade();
    }

    public void switchLights() {
        this.playerModel.switchLights();
    }

    public void flashLight() {
        this.playerModel.flashLight();
    }

    public void time() {
        this.playerModel.timer.getRemaining_time();
    }

    public void commands() {
        this.playerModel.notify_player("Available commands:");
        String type = this.getType();
        switch (type + "") {
            case "door":
                this.playerModel.notify_player(this.DOOR_COMMANDS.toString());
                break;
            case "seller":
                this.playerModel.notify_player(this.SELLER_COMMANDS.toString());
                break;
            case "mirror":
                this.playerModel.notify_player(this.MIRROR_COMMANDS.toString());
                break;
            case "painting":
                this.playerModel.notify_player(this.PAINTING_COMMANDS.toString());
                break;
            case "chest":
                this.playerModel.notify_player(this.CHEST_COMMANDS.toString());
                break;
            default:
                ArrayList<String> keys = new ArrayList<>();
                keys.addAll(this.commandsMap.keySet());
                Collections.sort(keys);
                this.playerModel.notify_player(keys.toString());
        }
    }

    public void switchHints() {
        this.hint = !this.hint;
        this.playerModel.notify_player("Hints are " + (this.hint ? "on" : "off"));
    }

    public void restart() {
        try {
            this.playerModel.menu.restart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        this.playerModel.menu.quit();
    }

    public void use_method(String command) {
        Command c = this.commandsMap.get(command);
        if (c == null) {
            c = this.shortCommandsMap.get(command);
        }
        if (c == null) {
            this.playerModel.notify_player("Use a valid command");
            this.commands();
        } else {
            c.applyCommand(this);
        }
    }

    @Override
    public String toString() {
        return "Player Controller Master";
    }
}