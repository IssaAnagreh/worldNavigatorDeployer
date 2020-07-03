package com.worldNavigator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MenuServer")
public class MapFactory extends HttpServlet {
  private String name = "name empty";
  public List<Room> rooms = new ArrayList<>();
  public int endTime;
  private int room_counter = 0;
  public String mapName = "mapName empty";
  public Map<String, Object> contents;
  public String location;
  public String orientation;
  public int roomIndex;
  public JSONArray jsonRooms;

  private static final long serialVersionUID = 1L;

  private JSONObject castToJSONObject(Object o) {
    return (JSONObject) o;
  }

  private JSONArray castToJSONArray(Object o) {
    return (JSONArray) o;
  }

  /**
   * @see HttpServlet#HttpServlet()
   */
  public MapFactory() {
    super();
    // TODO Auto-generated constructor stub
  }

  @SuppressWarnings("unchecked")
  public MapFactory(String mapName) {
    this.mapName = mapName;
    this.name = "MapFactory";
    // JSON parser object to parse read file
    JSONParser jsonParser = new JSONParser();

    File file = new File("maps.json");
    try (FileReader reader = new FileReader("map.json")) {
      // Read JSON file
      Object obj = jsonParser.parse(reader);

      JSONArray maps = castToJSONArray(obj);
      maps.forEach(map -> parseMapObject(castToJSONObject(map)));

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      this.name = "FileNotFoundException";
    } catch (IOException e) {
      e.printStackTrace();
      this.name = "IOException";
    } catch (ParseException e) {
      e.printStackTrace();
      this.name = "ParseException";
    }
  }

  private void parseMapObject(JSONObject map) {
    if (map == null) {
      throw new IllegalArgumentException();
    } else {
      // Get map object within list
      name = (String) map.get("name");
      endTime = Integer.parseInt(map.get("end_time").toString());
      ContentManager contentManager = new ContentManager();
      String player_string = "player";
      HashMap<String, Object> player_details = (HashMap) map.get(player_string);
      contentManager.managePlayer(player_details);
      this.contents = contentManager.getContents();
      this.location =
          (player_details).get("location") != null
              ? (player_details).get("location").toString()
              : "c3";
      this.orientation =
          (player_details).get("orientation") != null
              ? (player_details).get("orientation").toString()
              : "north";
      this.roomIndex =
          (player_details).get("roomIndex") != null
              ? Integer.parseInt((player_details).get("roomIndex").toString())
              : 0;

      this.jsonRooms = castToJSONArray(map.get("rooms"));
      this.jsonRooms.forEach(room -> parseRoomObject(castToJSONObject(room)));
    }
  }

  private void parseRoomObject(JSONObject room) {
    // Get room object within list
    JSONObject roomObject;
    try {
      roomObject = castToJSONObject(room.get("room"));
    } catch (Exception e) {
      throw new NullPointerException();
    }
    this.rooms.add(new Room(roomObject, room_counter));
    this.room_counter++;
  }

  @Override
  public String toString() {
    return name;
  }


  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    new MapFactory("Min jowa al get");
    // Step 1: set content type
    response.setContentType("text/html");

    // Step 2: get the printwriter
    PrintWriter out = response.getWriter();

    // Step 3: generate the HTML content
    out.println("<html><body>");

    out.println("Map Name: "
            + this.mapName);

    out.println("</body></html>");

  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
    doGet(request, response);
  }

}
