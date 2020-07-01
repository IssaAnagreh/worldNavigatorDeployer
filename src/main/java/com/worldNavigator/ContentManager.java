package com.worldNavigator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentManager {
  private Map<String, Object> contents = new HashMap<>();

  public void manageItem(JSONObject item) {
    JSONObject content = (JSONObject) item.get("content");
    if (content != null) {
      for (ContentsTypes contentType : ContentsTypes.values()) {
        if (content.get(contentType.toString()) != null) {
          if (contentType.toString().equals("keys")) {
            JSONArray keys_names = (JSONArray) content.get(contentType.toString());
            List<Key> keys = new ArrayList<>();
            if (keys_names != null) {
              keys_names.forEach(emp -> keys.add(new Key(emp.toString())));
              this.contents.put(contentType.toString(), keys);
            }
          } else {
            int single_content = Integer.parseInt(content.get(contentType.toString()).toString());
            this.contents.put(contentType.toString(), single_content);
          }
        }
      }
    }
  }

  public void manageSellerItem(JSONObject seller) {
    this.contents = (HashMap) seller.get("content");
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

  @Override
  public String toString() {
    return this.getContents().toString();
  }
}
