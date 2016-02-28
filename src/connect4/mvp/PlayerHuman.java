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
public class PlayerHuman implements Player {
  private final String name = "You";
  
  @Override
  public String getName() {
    return this.name;
  }
  
  @Override
  public boolean isAI() {
    return false;
  }

  @Override
  public void makeMove(MatchPresenter controller, MatchModel model) {
    // Human player will actually make a move. Nothing else to do.
  }
}
