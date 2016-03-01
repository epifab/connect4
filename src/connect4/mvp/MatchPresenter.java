/*
 * Copyright (C) 2016 fabio.epifani
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package connect4.mvp;

/**
 * Match presenter. Handles model and view.
 * @author fabio.epifani
 */
public class MatchPresenter {
  final MatchModel model;
  final MatchView view;
  private final int ROWS = 7;
  private final int COLUMNS = 8;
  private final Player player1;
  private final Player player2;

  public MatchPresenter(MatchView view) {
    this(view, new PlayerHuman(), new PlayerRobot());
  }
  
  public MatchPresenter(MatchView view, Player player1, Player player2) {
    this.view = view;
    this.player1 = player1;
    this.player2 = player2;
    this.model = new MatchModel(player1.getName(), player2.getName(), this.ROWS, this.COLUMNS);
  }
  
  public void init() {
    this.view.newMatch(model);
    
    if (player1 instanceof PlayerRobot) {
      PlayerRobot player = (PlayerRobot)player1;
      this.makeMove(player.getBestMove(model));
    }
  }

  /**
   * Makes a move and updates model and the view.
   * @param col 0-based column index
   */
  public void makeMove(int col) {
    int currentPlayer = this.model.currentPlayer;

    int row = this.model.makeMove(col);
    
    this.view.makeMove(currentPlayer, new Point(row, col));
    
    switch (this.model.status) {
      case Active:
        if (row == this.model.board.rows - 1) {
          this.view.disableColumn(col);
        }
        this.view.setCurrentPlayer(this.model.currentPlayer);
        
        Player player = this.model.currentPlayer == 1 ? this.player1 : this.player2;
        
        if (player instanceof PlayerRobot) {
          PlayerRobot robot = (PlayerRobot)player;
          int robotMove = robot.getBestMove(this.model);
          this.makeMove(robotMove);
        }
        
        break;
        
      case Tie:
        this.view.endMatch();
        break;
        
      case Winner:
        this.view.endMatch(this.model.winner, this.model.connected);
        break;
    }    
  }
}
