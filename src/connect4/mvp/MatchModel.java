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

import java.util.Stack;

/**
 *
 * @author fabio.epifani
 */
public class MatchModel {
  public enum Status {
    Active,
    Tie,
    Winner
  }
  public final String player1;
  public final String player2;
  public int currentPlayer;
  public final Board board;
  public Status status;
  public Point[] connected;
  public int winner;
  private final Stack<Point> moves;
  private final int connect_number = 4;
  
  public MatchModel(String player1, String player2, int rows, int columns) {
    this.player1 = player1;
    this.player2 = player2;
    this.currentPlayer = 1;
    this.board = new Board(rows, columns);
    this.status = Status.Active;
    this.moves = new Stack();
  }
  
  /**
   * Makes a move.
   * @param col 0-based column index
   * @return 0-based row index (where the disc was inserted).
   */
  int makeMove(int col) {
    if (this.status != Status.Active) {
      throw new IllegalArgumentException("The match is over");
    }
    
    int row = this.getNextFreeSlot(col);
    if (row == -1) {
      throw new IllegalArgumentException("Column is full");    
    }
    
    this.makeMove(row, col);
    return row;
  }
  
  /**
   * Makes a move and updates the model.
   * @param row 0-based row index
   * @param col 0-based column index
   */
  private void makeMove(int row, int col) {
    this.board.set(row, col, this.currentPlayer);

    // Winner detection
    this.connected = this.getConnected(row, col);
    
    if (this.connected != null) {
      this.status = MatchModel.Status.Winner;
      this.winner = this.board.get(this.connected[0]);
    }
    
    else if (this.isTie()) {
      this.status = MatchModel.Status.Tie;
    }
    
    this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;
    
    this.moves.add(new Point(row, col));
  }
  
  /**
   * Undo the last move.
   */
  void undoMove() {
    Point move = this.moves.pop();
    this.board.set(move.row, move.column, 0);
    this.status = MatchModel.Status.Active;
    this.winner = 0;
    this.connected = null;
    this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;
  }
  
  /**
   * Gets the next free cell
   * @param col 0-based column index
   * @return 0-based row index or -1 if no slots are available
   */
  private int getNextFreeSlot(int col) {
    for (int row = 0; row < this.board.rows; row++) {
      if (this.board.get(row, col) == 0) {
        return row;
      }
    }
    return -1;
  }
  
  /**
   * Checks whether there are still empty cells.
   * @return True if no more cells are available
   */
  private boolean isTie() {
    for (int column = 0; column < this.board.rows; column++) {
      if (this.board.get(this.board.rows - 1, column) == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Detects 4 connected discs intercepting the given row / column cell.
   * @param row 0-based row index
   * @param col 0-based col index
   * @return Array of connected points or null if not found
   */
  private Point[] getConnected(int row, int col) {
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
    
    int playerId = this.board.get(row, col);

    if (playerId == 0) {
      return null;
    }

    n = 0;
    minRow = row - this.connect_number - 1;  // starting point
    maxRow = row + this.connect_number - 1;  // arriving point

    if (minRow < 0) {
      minRow = 0;
    }
    if (maxRow >= this.board.rows) {
      maxRow = this.board.rows - 1;
    }

    // Search for a vertical "4 in a row" line
    for (i = minRow; i <= row; i++) {
      if (this.board.get(i, col) != playerId) {
        // Checks whether it's still possible to close the match
        if (maxRow - i < this.connect_number) {
          break;
        }

        n = 0;
      }
      else {
        n++;

        if (n == 1) {
          winRowStart = i;
        }
        else if (n == this.connect_number) {
          winRowStop = i;
          detected = true;
          break;
        }
      }
    }

    if (detected) {
      return this.createConnected(winRowStart, col, winRowStop, col);
    }

    // Search for an horizontal "4 in a line"
    n = 0;
    minCol = col - this.connect_number - 1;
    maxCol = col + this.connect_number - 1;

    if (minCol < 0) {
      minCol = 0;
    }
    if (maxCol >= this.board.columns) {
      maxCol = this.board.columns - 1;
    }

    for (i = minCol; i <= maxCol; i++) {
      if (this.board.get(row, i) != playerId) {
        // Checks whether it's still possible to close the match
        if (maxCol - i < this.connect_number) {
          break;
        }

        n = 0;
      }
      else {
        n++;
        if (n == 1) {
          winColStart = i;
        }
        else if (n == this.connect_number) {
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
    tlCorner = Math.min(col - minCol, maxRow - row);
    brCorner = Math.min(maxCol - col, row - minRow);
    trCorner = Math.min(maxCol - col, maxRow - row);
    blCorner = Math.min(col - minCol, row - minRow);

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
    if (tlCorner + brCorner >= this.connect_number - 1) {
      minCol = col - tlCorner;
      minRow = row + tlCorner;

      for (i = 0; i <= tlCorner + brCorner; i++) {
        // checking from left-to-right, top to bottom:
        // Increasing the column, decreasing the row
        if (this.board.get(minRow - i, minCol + i) == playerId) {
          n++;
          if (n == 1) {
            winRowStart = minRow - i;
            winColStart = minCol + i;
          }
          else if (n == this.connect_number) {
            winRowStop = minRow - i;
            winColStop = minCol + i;
            detected = true;
            break;
          }
        }
        else {
          n = 0;
          if (tlCorner + brCorner - i < this.connect_number) {
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
    if (trCorner + blCorner >= this.connect_number - 1) {
      // aggiorno i valori di minCol e minRow
      minCol = col - blCorner;
      minRow = row - blCorner;

      for (i = 0; i <= blCorner + trCorner; i++) {
        // checking from left-to-right, bottom to top:
        // Increasing both column and row
        if (this.board.get(minRow + i, minCol + i) == playerId) {
          n++;
          if (n == 1) {
            winColStart = minCol + i;
            winRowStart = minRow + i;
          }
          else if (n == this.connect_number) {
            winColStop = minCol + i;
            winRowStop = minRow + i;
            detected = true;
            break;
          }
        } else {
          n = 0;
          if (trCorner + blCorner - i < this.connect_number) {
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

  /**
   * Creates an array of points intercepting the 2 given coordinate.
   * @param row1 0-based row index (1st point)
   * @param col1 0-based column index (1st point)
   * @param row2 0-based row index (2nd point)
   * @param col2 0-based column index (2nd point)
   * @return Array of connected points
   */
  private Point[] createConnected(int row1, int col1, int row2, int col2) {
    Point[] connected = new Point[this.connect_number];

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

    for (int i = 0; i < this.connect_number; i++) {
      connected[i] = new Point(row2 + (rowInc * i), col2 + (colInc * i));
    }
    
    return connected;
  }
}
