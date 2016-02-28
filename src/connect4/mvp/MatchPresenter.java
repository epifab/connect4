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
 *
 * @author fabio.epifani
 */
public class MatchPresenter {
  private final int ROWS = 6;
  private final int COLUMNS = 7;
  private final MatchView view;
  private final MatchModel model;
  private final Player player1;
  private final Player player2;

  public MatchPresenter(MatchView view) {
    this(new PlayerHuman(), new PlayerRobot(), view);
  }
  
  public MatchPresenter(Player player1, Player player2, MatchView view) {
    this.player1 = player1;
    this.player2 = player2;
    this.view = view;
    this.model = new MatchModel(player1.getName(), player2.getName(), this.ROWS, this.COLUMNS);
  }

  public void init(boolean humanMovesFirst) {
    this.view.newMatch(this.model);
  }
  
  public void makeMove(int col) {
    int currentPlayer = this.model.currentPlayer;
    
    int row = this.getNextFreeSlot(col);
    
    if (row == -1) {
      throw new IllegalArgumentException("Column is full");    
    }
    
    // Updates the model
    makeMove(row, col);
    
    this.view.makeMove(this.model.currentPlayer, new Point(row, col));
    
    switch (this.model.status) {
      case Active:
        this.view.makeMove(currentPlayer, new Point(row, col));
        if (row == this.model.board.rows - 1) {
          this.view.disableColumn(col);
        }
        this.view.setCurrentPlayer(this.model.currentPlayer);
        break;
        
      case Tie:
        this.view.endMatch();
        break;
        
      case Winner:
        this.view.endMatch(this.model.winner, this.model.connected);
        break;
    }
  }
  
  private void makeMove(int row, int column) {
    this.model.board.set(row, column, this.model.currentPlayer);

    // Winner detection
    this.model.connected = this.getConnected(row, column);
    
    if (this.model.connected != null) {
      this.model.status = MatchModel.Status.Winner;
      this.model.winner = this.model.board.get(this.model.connected[0]);
    }
    
    else if (this.isTie()) {
      this.model.status = MatchModel.Status.Tie;
    }
    
    this.model.currentPlayer = this.model.currentPlayer == 1 ? 2 : 1;
  }
  
  private void makeMoveAI() {
    // Artificial intelligence logic here...
    
  }
  
  int getNextFreeSlot(int column) {
    for (int row = 0; row < this.model.board.rows; row++) {
      if (this.model.board.get(row, column) == 0) {
        return row;
      }
    }
    return -1;
  }
  
  private boolean isTie() {
    for (int column = 0; column < this.model.board.rows; column++) {
      if (this.model.board.get(this.model.board.rows - 1, column) == 0) {
        return false;
      }
    }
    return true;
  }

  private Point[] getConnected(int row, int column) {
    boolean detected = false;

    int winColStart = 0;
    int winColStop = 0;
    int winRowStart = 0;
    int winRowStop = 0;

    int minCol, maxCol;
    int minRow, maxRow;
    int tlCorner, brCorner;
    int blCorner, trCorner;
    int i, n;
    
    int playerId = this.model.board.get(row, column);

    if (playerId == 0) {
      return null;
    }

    n = 0;
    minRow = row - 3;  // starting point
    maxRow = row + 3;  // arriving point

    if (minRow < 0) {
      minRow = 0;
    }
    if (maxRow >= this.model.board.rows) {
      maxRow = this.model.board.rows - 1;
    }

    // Search for a vertical "4 in a row" line
    for (i = minRow; i <= row; i++) {
      if (this.model.board.get(i, column) != playerId) {
        // Checks whether it's still possible to close the match
        if (maxRow - i < 4) {
          break;
        }

        n = 0;
      }
      else {
        n++;

        if (n == 1) {
          winRowStart = i;
        }
        else if (n == 4) {
          winRowStop = i;
          detected = true;
          break;
        }
      }
    }

    if (detected) {
      return this.createConnected(winRowStart, column, winRowStop, column);
    }

    // Search for an horizontal "4 in a line"
    n = 0;
    minCol = column - 3;
    maxCol = column + 3;

    if (minCol < 0) {
      minCol = 0;
    }
    if (maxCol >= this.model.board.columns) {
      maxCol = this.model.board.columns - 1;
    }

    for (i = minCol; i <= maxCol; i++) {
      if (this.model.board.get(row, i) != playerId) {
        // Checks whether it's still possible to close the match
        if (maxCol - i < 4) {
          break;
        }

        n = 0;
      }
      else {
        n++;
        if (n == 1) {
          winColStart = i;
        }
        else if (n == 4) {
          winColStop = i;
          detected = true;
          break;
        }
      }
    }

    if (detected) {
      return this.createConnected(row, winColStart, row, winColStop);
    }

    /*
     * tlCorner: top left corner
     * brCorner: bottom right corner
     * trCorner: top right corner
     * blCorner: bottom left corner
     */
    tlCorner = Math.min(column - minCol, maxRow - row);
    brCorner = Math.min(maxCol - column, row - minRow);
    trCorner = Math.min(maxCol - column, maxRow - row);
    blCorner = Math.min(column - minCol, row - minRow);

    n = 0;

    /*
     *     0   1   2   3   4   5
     *   +---+---+---+---+---+---+
     * 3 | X |   |   |   |   |   |
     *   +---+---+---+---+---+---+
     * 2 | o | X |   |   |   |   |
     *   +---+---+---+---+---+---+
     * 1 | x | o | X | o |   |   |
     *   +---+---+---+---+---+---+
     * 0 | o | o | x | X | o |   |
     *   +---+---+---+---+---+---+
     */
    if (tlCorner + brCorner >= 3) {
      minCol = column - tlCorner;
      minRow = row + tlCorner;

      for (i = 0; i <= tlCorner + brCorner; i++) {
        // checking from left-to-right, top to bottom:
        // Increasing the column, decreasing the row
        if (this.model.board.get(minRow - i, minCol + i) == playerId) {
          n++;
          if (n == 1) {
            winRowStart = minRow - i;
            winColStart = minCol + i;
          }
          else if (n == 4) {
            winRowStop = minRow - i;
            winColStop = minCol + i;
            detected = true;
            break;
          }
        }
        else {
          n = 0;
          if (tlCorner + brCorner - i < 4) {
            break;
          }
        }
      }
    }
    
    if (detected) {
      return this.createConnected(winRowStart, winColStart, winRowStop, winColStop);
    }

    /*
     *     0   1   2   3   4   5
     *   +---+---+---+---+---+---+
     * 3 |   |   |   | O |   |   |
     *   +---+---+---+---+---+---+
     * 2 | o | x | O | x |   |   |
     *   +---+---+---+---+---+---+
     * 1 | x | O | x | o | x |   |
     *   +---+---+---+---+---+---+
     * 0 | O | o | x | x | o |   |
     *   +---+---+---+---+---+---+
     */
    if (trCorner + blCorner >= 3) {
      // aggiorno i valori di minCol e minRow
      minCol = column - blCorner;
      minRow = row - blCorner;

      for (i = 0; i <= blCorner + trCorner; i++) {
        // checking from left-to-right, bottom to top:
        // Increasing both column and row
        if (this.model.board.get(minRow + i, minCol + i) == playerId) {
          n++;
          if (n == 1) {
            winColStart = minCol + i;
            winRowStart = minRow + i;
          }
          else if (n == 4) {
            winColStop = minCol + i;
            winRowStop = minRow + i;
            detected = true;
            break;
          }
        } else {
          n = 0;
          if (trCorner + blCorner - i < 4) {
            break;
          }
        }
      }
    }
    if (detected) {
      return this.createConnected(winRowStart, winColStart, winRowStop, winColStop);
    }

    return null;
  }

  private Point[] createConnected(int row1, int col1, int row2, int col2) {
    Point[] slots = new Point[4];

    int colInc = 0;
    
    if (col1 < col2) {
      colInc = -1;
    }
    else if (col1 > col2) {
      colInc = 1;
    }
    
    int rowInc = 0;
    
    if (row1 < row2) {
      rowInc = -1;
    }
    else if (row1 > row2) {
      rowInc = 1;
    }

    slots[0] = new Point(row2, col2);
    slots[1] = new Point(row2 + rowInc, col2 + colInc);
    slots[2] = new Point(row2 + (rowInc * 2), col2 + (colInc * 2));
    slots[3] = new Point(row2 + (rowInc * 3), col2 + (colInc * 3));
    
    return slots;
  }
}
