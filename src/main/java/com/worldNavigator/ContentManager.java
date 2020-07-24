package com.worldNavigator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentManager {
  private Map<String, Object> contents = new HashMap<>();
  public Map<String, Object> contentsStrings = new HashMap<>();

  public void manageItem(JSONObject item) {
    JSONObject content = (JSONObject) item.get("contents");
    if (content != null) {
      for (ContentsTypes contentType : ContentsTypes.values()) {
        if (content.get(contentType.toString()) != null) {
          if (contentType.toString().equals("keys")) {
            JSONArray keys_names = (JSONArray) content.get(contentType.toString());
            List<Key> keys = new ArrayList<>();
            List<String> keysString = new ArrayList<>();
            if (keys_names != null) {
              keys_names.forEach(emp -> keys.add(new Key(emp.toString())));
              keys_names.forEach(emp -> keysString.add(new Key(emp.toString()).toString()));
              this.contents.put(contentType.toString(), keys);
              this.contentsStrings.put(contentType.toString(), keysString);
            }
          } else {
            int single_content = Integer.parseInt(content.get(contentType.toString()).toString());
            this.contents.put(contentType.toString(), single_content);
            this.contentsStrings.put(contentType.toString(), single_content);
          }
        }
      }
    }
  }

  public void manageSellerItem(JSONObject seller) {
    this.contents = (HashMap) seller.get("contents");
    this.contentsStrings = (HashMap) seller.get("contents");

    if (this.contents != null) {
      for (String contentKey : contents.keySet()) {
        if (contentKey.equals("keys")) {
          JSONArray temp = (JSONArray) contents.get(contentKey);
          if (temp != null) {
            temp.forEach(
                emp -> {
                  HashMap<String, Object> key = (HashMap<String, Object>) emp;
                  key.replace("name", new Key(key.get("name").toString()));
                  key.replace("cost", new Key(key.get("cost").toString()));
                });
          }
        }
      }
    }
  }

  public void managePlayer(Map<String, Object> player) {
    for (ContentsTypes c : ContentsTypes.values()) {
      if (player.get(c.toString()) != null) {
        int single_content = Integer.parseInt(player.get(c.toString()).toString());
        this.contents.put(c.toString(), single_content);
      }
    }
    this.contents.put("keys", new ArrayList<Key>());
  }

  public Map<String, Object> getContents() {
    return this.contents;
  }

  public int calculatePower() {
    int power = 0;
    for (Object key: this.contents.keySet()) {
      if (key == "keys") {
        power += 10;
      } else if (key == "golds") {
        power += (int) this.contents.get(key);
      } else if (key == "flashLights") {
        power += ((int) this.contents.get(key) * 2);
      }
    }
    return power;
  }

  @Override
  public String toString() {
    return this.getContents().toString();
  }
}
