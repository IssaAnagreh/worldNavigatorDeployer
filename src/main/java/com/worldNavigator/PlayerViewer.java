package com.worldNavigator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

@WebServlet("/StudentServlet")
public class PlayerViewer extends HttpServlet implements Observer {
  private static final long serialVersionUID = 1L;
  public PlayerControllerInterface playerController;
  private String name;
  private String output = "Empty";

  /**
   * @see HttpServlet#HttpServlet()
   */
  public PlayerViewer() {
    super();
    // TODO Auto-generated constructor stub
  }

  public PlayerViewer(PlayerControllerInterface playerController, String name) {
    super();
    this.playerController = playerController;
    this.name = name;
    this.playerController.subscribe(this);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // Step 1: set content type
    response.setContentType("text/html");

    // Step 2: get the printwriter
    PrintWriter out = response.getWriter();


    String command = request.getParameter("command");

    Maps maps = new Maps();
    maps.addMap("map.json");

    Menu menu = new Menu();
    menu.setMaps(maps, "0");
    menu.start(this);

    this.playerController.use_command(command);
    output = this.playerController.toString();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
      output = "sleep error";
    }

    // Step 3: generate the HTML content
    out.println("<html><body>");

    out.println("Command is: "
            + command);
    out.println("Output is: " + output);

    out.println("</body></html>");

  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // TODO Auto-generated method stub
    doGet(request, response);
  }

  @Override
  public void update(Observable o, Object arg) {
    PlayerModel playerModel = (PlayerModel) o;
    String msg = (String) arg;
    this.output = msg;
    if (playerModel.consoleColor == null) {
      if (playerModel.isInline) {
        System.out.print(msg);
        this.output = msg;
      } else {
        System.out.println(msg);
      }
    } else {
      update(o, msg, playerModel.consoleColor);
    }
  }

  public void update(Observable o, String msg, ConsoleColors color) {
    String ANSI;
    String ANSI_RESET = "\u001B[0m";
    if (color == ConsoleColors.black) {
      ANSI = "\u001B[30m";
    } else if(color == ConsoleColors.red) {
      ANSI = "\u001B[31m";
    }
    else if(color == ConsoleColors.green) {
      ANSI = "\u001B[32m";
    }
    else if(color == ConsoleColors.yellow) {
      ANSI = "\u001B[33m";
    }
    else if(color == ConsoleColors.blue) {
      ANSI = "\u001B[34m";
    }
    else if(color == ConsoleColors.purple) {
      ANSI = "\u001B[35m";
    }
    else if(color == ConsoleColors.cyan) {
      ANSI = "\u001B[36m";
    }
    else if(color == ConsoleColors.white) {
      ANSI = "\u001B[37m";
    } else {
      ANSI = "\u001B[0m";
    }

    PlayerModel playerModel = (PlayerModel) o;

    if (playerModel.isInline) {
      System.out.print(ANSI + msg + ANSI_RESET);
    } else {
      System.out.println(ANSI + msg + ANSI_RESET);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Player viewer";
  }
}
