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
package connect4.MVP;

/**
 *
 * @author fabio.epifani
 */
public class MatchModel {
  public final Player player1;
  public final Player player2;
  public int currentPlayerId;
  public final int columns;
  public final int rows;
  public final int[][] board;
  
  public MatchModel(Player player1, Player player2) {
    this(player1, player2, 7, 6);
  }
  
  public MatchModel(Player player1, Player player2, int columns, int rows) {
    this.player1 = player1;
    this.player2 = player2;
    this.currentPlayerId = 1;
    this.columns = columns;
    this.rows = rows;
    this.board = new int[columns][rows];
    for (int x = 0; x < columns; x++) {
      this.board[x] = new int[rows];
    }
  }
}
