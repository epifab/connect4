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
 * View
 * 
 * @author fabio.epifani
 */
public interface MatchView {
  public void newMatch(String player1, String player2, int rows, int columns);
  
  public void setCurrentPlayer(int currentPlayer);
  
  public void makeMove(int player, Point point);
  
  public void disableColumn(int column);
  
  public void endMatch();
  
  public void endMatch(int winner, Point[] connected);
}
