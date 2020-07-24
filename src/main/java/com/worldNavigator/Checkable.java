package com.worldNavigator;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public abstract class Checkable implements CheckBehavior {
  private ContentManager contents;
  protected final String LOCATION;

  public Checkable(String location) {
    this.LOCATION = location;
  }

  protected void setContents(JSONObject item) {
    this.contents = new ContentManager();
    this.contents.manageItem(item);
  }

  public ContentManager getContents() {
    return contents;
  }

  public void acquireContents(String location, PlayerModel playerModel) {
    Map<String, Object> acquiredContents = this.checkContent(location, playerModel);
    if (!acquiredContents.isEmpty()) {
      for (ContentsTypes contentType : ContentsTypes.values()) {
        if (acquiredContents.get(contentType.toString()) != null) {
          if (contentType.toString().equals("keys")) {
            ((List<?>) acquiredContents.get("keys"))
                .forEach(((List) playerModel.getContent("keys"))::add);
          } else {
            playerModel.addToContents(
                contentType.toString(),
                (playerModel.getContent(contentType.toString()) == null
                        ? 0
                        : (int) playerModel.getContent(contentType.toString()))
                    + ((int) acquiredContents.get(contentType.toString())));
          }
        }
      }
      playerModel.notify_player("Contents acquired " + acquiredContents);
    } else {
      playerModel.notify_player("Nothing to acquire", ConsoleColors.red);
    }
  }
}
