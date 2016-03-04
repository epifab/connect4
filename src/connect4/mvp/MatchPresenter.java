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
 * Presenter
 * 
 * @author fabio.epifani
 */
public class MatchPresenter {
  private MatchModel model;
  private final MatchView view;
  private final int ROWS = 7;
  private final int COLUMNS = 8;
  private Player player1;
  private Player player2;

  public MatchPresenter(MatchView view) {
    this.view = view;
  }
  
  public void newMatch() {
    this.newMatch(new PlayerHuman(), new PlayerRobot(), ROWS, COLUMNS);
  }
  
  /**
   * initialises a new match.
   * @param player1 Player 1
   * @param player2 Player 2
   * @param rows Number of rows
   * @param columns Number of columns
   */
  public void newMatch(Player player1, Player player2, int rows, int columns) {
    this.model = new MatchModel(player1, player2, rows, columns);

    this.view.newMatch(player1.getName(), player2.getName(), rows, columns);
    
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
    int currentPlayerId = this.model.getCurrentPlayerId();

    int row = this.model.makeMove(col);
    
    this.view.makeMove(currentPlayerId, new Point(row, col));
    
    switch (this.model.getStatus()) {
      case Active:
        if (row == this.model.board.rows - 1) {
          this.view.disableColumn(col);
        }
        this.view.setCurrentPlayer(this.model.getCurrentPlayerId());
        
        Player player = this.model.getCurrentPlayer();
        
        if (player instanceof PlayerRobot) {
          PlayerRobot robot = (PlayerRobot)player;
          this.makeMove(robot.getBestMove(this.model));
        }
        
        break;
        
      case Tie:
        this.view.endMatch();
        break;
        
      case Winner:
        this.view.endMatch(this.model.getWinner(), this.model.getConnected());
        break;
    }    
  }
}
