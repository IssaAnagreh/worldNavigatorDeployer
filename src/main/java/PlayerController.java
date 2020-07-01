 public class PlayerController extends PlayerControllerMaster {
  public PlayerController(PlayerModel playerModel) {
    super.playerModel = playerModel;
    super.shortCommandsMap.put("o", PlayerControllerInterface::myOrientation);
    super.shortCommandsMap.put("loc", PlayerControllerInterface::myLocation);
    super.shortCommandsMap.put("l", PlayerControllerInterface::rotateLeft);
    super.shortCommandsMap.put("r", PlayerControllerInterface::rotateRight);
    super.shortCommandsMap.put("f", playerController -> move(MoveTypes.forward));
    super.shortCommandsMap.put("b", playerController -> move(MoveTypes.backward));
    super.shortCommandsMap.put(
        "c",
        playerController -> {
          this.check();
          this.acquire_items();
        });
    super.shortCommandsMap.put("items", PlayerControllerInterface::myItems);
    super.shortCommandsMap.put("key", PlayerControllerInterface::use_key);
    super.shortCommandsMap.put("light", PlayerControllerInterface::switchLights);
    super.shortCommandsMap.put("flash", PlayerControllerInterface::flashLight);
    super.shortCommandsMap.put("master", playerController -> super.playerModel.use_masterKey());
    super.shortCommandsMap.put("setloc", PlayerControllerInterface::setLocation);
  }

  public PlayerController() {}

  @Override
  public String toString() {
    return "Player Controller";
  }
}
