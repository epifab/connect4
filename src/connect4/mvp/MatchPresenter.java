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
  private MatchView view;
  private MatchModel model;

  public MatchPresenter(MatchView view) {
    this.view = view;
  }

  public void init(boolean humanMovesFirst) {
    Player human = new PlayerHuman();
    Player robot = new PlayerRobot();

    this.model = humanMovesFirst
      ? new MatchModel(human, robot)
      : new MatchModel(robot, human);

    this.view.show(this.model);
  }

  public void makeMove(int x) {
    for (int y = 0; y < this.model.board[x].length; y++) {
      if (this.model.board[x][y] == 0) {
        makeMove(x, y);
        return;
      }
    }

    throw new IllegalArgumentException("Column is full");
  }    
  
  private void makeMove(int x, int y) {
    boolean matchIsOver = false;
    
    this.model.board[x][y] = this.model.currentPlayerId;

    this.view.makeMove(this.model.currentPlayerId, x, y);

    BoardSlot[] fourInALine = this.getFourInALine(x, y);
    if (fourInALine != null) {
      matchIsOver = true;
      this.view.endMatch(this.model.board[x][y], fourInALine);
    }

    if (y == this.model.board[x].length - 1) {
      // This column has now been filled up
      this.view.disableColumn(x);

      if (!matchIsOver) {
        // If every column has been filled up, we have a tie
        boolean allDisabled = true;

        for (int i = 0; i < this.model.columns; i++) {
          if (this.model.board[i][this.model.rows - 1] == 0) {
            allDisabled = false;
            break;
          }
        }

        if (allDisabled) {
          matchIsOver = true;
          this.view.endMatch();
        }
      }
    }
    
    if (!matchIsOver) {
      this.model.currentPlayerId = this.model.currentPlayerId == 1 ? 2 : 1;
      this.view.setCurrentPlayerId(this.model.currentPlayerId);
      
      Player currentPlayer = this.model.currentPlayerId == 1
        ? this.model.player1
        : this.model.player2;
      
      if (currentPlayer.isAI()) {
        this.makeMoveAI();
      }
    }
  }
  
  private void makeMoveAI() {
    // Artificial intelligence logic here...
  }
  
  private BoardSlot[] getFourInALine(int x, int y) {
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
    
    int playerId = this.model.board[x][y];

    if (playerId == 0) {
      return null;
    }

    n = 0;
    minRow = y - 3;  // starting point
    maxRow = y + 3;  // arriving point

    if (minRow < 0) {
      minRow = 0;
    }
    if (maxRow >= this.model.rows) {
      maxRow = this.model.rows - 1;
    }

    // Search for a vertical "4 in a row" line
    for (i = minRow; i <= y; i++) {
      if (this.model.board[x][i] != playerId) {
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
      return this.createFourInALine(x, winRowStart, x, winRowStop);
    }

    // Search for an horizontal "4 in a line"
    n = 0;
    minCol = x - 3;
    maxCol = x + 3;

    if (minCol < 0) {
      minCol = 0;
    }
    if (maxCol >= this.model.columns) {
      maxCol = this.model.columns - 1;
    }

    for (i = minCol; i <= maxCol; i++) {
      if (this.model.board[i][y] != playerId) {
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
      return this.createFourInALine(winColStart, y, winColStop, y);
    }


    /*
     * tlCorner: top left corner
     * brCorner: bottom right corner
     * trCorner: top right corner
     * blCorner: bottom left corner
     */
    tlCorner = Math.min(x - minCol, maxRow - y);
    brCorner = Math.min(maxCol - x, y - minRow);
    trCorner = Math.min(maxCol - x, maxRow - y);
    blCorner = Math.min(x - minCol, y - minRow);

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
      minCol = x - tlCorner;
      minRow = y + tlCorner;

      for (i = 0; i <= tlCorner + brCorner; i++) {
        // checking from left-to-right, top to bottom:
        // Increasing the column, decreasing the row
        if (this.model.board[minCol + i][minRow - i] == playerId) {
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
      return this.createFourInALine(winColStart, winRowStart, winColStop, winRowStop);
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
      minCol = x - blCorner;
      minRow = y - blCorner;

      for (i = 0; i <= blCorner + trCorner; i++) {
        // checking from left-to-right, bottom to top:
        // Increasing both column and row
        if (this.model.board[minCol + i][minRow + i] == playerId) {
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
      return this.createFourInALine(winColStart, winRowStart, winColStop, winRowStop);
    }

    return null;
  }

  private BoardSlot[] createFourInALine(int x1, int y1, int x2, int y2) {
    BoardSlot[] slots = new BoardSlot[4];

    int yIncrement = 0;
    
    if (y1 < y2) {
      yIncrement = -1;
    }
    else if (y1 > y2) {
      yIncrement = 1;
    }
    
    int xIncrement = 0;
    
    if (x1 < x2) {
      xIncrement = -1;
    }
    else if (x1 > x2) {
      xIncrement = 1;
    }

    slots[0] = new BoardSlot(x2, y2);
    slots[1] = new BoardSlot(x2 + xIncrement, y2 + yIncrement);
    slots[2] = new BoardSlot(x2 + (xIncrement * 2), y2 + (yIncrement * 2));
    slots[3] = new BoardSlot(x2 + (xIncrement * 3), y2 + (yIncrement * 3));
    
    return slots;
  }
}
