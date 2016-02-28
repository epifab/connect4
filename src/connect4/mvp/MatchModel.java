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
  
  public MatchModel(String player1, String player2, int rows, int columns) {
    this.player1 = player1;
    this.player2 = player2;
    this.currentPlayer = 1;
    this.board = new Board(rows, columns);
    this.status = Status.Active;
  }
}
