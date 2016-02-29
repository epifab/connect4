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

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fabio.epifani
 */
public class PlayerRobot implements Player {
  private final String name = "Hal";
  
  @Override
  public String getName() {
    return this.name;
  }

	/**
	 * The AI does think MAX_DEPTH moves ahead.
	 */
	static final int MAX_DEPTH = 8;

	/**
	 * The score given to a state that leads to a
	 * win.
	 */
	static final float WIN_REVENUE = 1f;

	/**
	 * The score given to a state that leads to a
	 * lose.
	 */
	static final float LOSE_REVENUE = -1f;

	/**
	 * The score given to a state that leads to a
	 * loss in the next turn
	 */
	static final float UNCERTAIN_REVENUE = 0f;

	/**
	 * Makes a turn.
	 * 
	 * @return The column where the turn was made.
	 *         Please note that the turn was
	 *         already made and doesn't have to be
	 *         done again.
	 */
	public int getBestMove(MatchModel match) {
		double maxValue = 2. * Integer.MIN_VALUE;
    ArrayList<Integer> moves = new ArrayList();

		// Search all columns for the one that has
		// the best value
		// The best score possible is WIN_REVENUE.
		// So if we find a move that has this
		// score, the search can be stopped.
		for (int col = 0; col < match.board.columns; col++) {
      try {
        match.makeMove(col);
        
        double value = this.alphabeta(match, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        
        match.undoMove();
        
        if (value > maxValue) {
          moves.clear();
          moves.add(col);
 					maxValue = value;
					if (value == WIN_REVENUE) {
						break;
					}         
        }
        
        else if (value == maxValue) {
          moves.add(col);
        }
      }
      catch (Exception ex) {
        // Column is full. Ignore this move.
      }
		}
    
    Random random = new Random();
    return moves.get(random.nextInt(moves.size()));
	}

	double alphabeta(MatchModel match, int depth, double alpha, double beta, boolean maximizingPlayer) {
		boolean hasWinner = match.status == MatchModel.Status.Winner;
		// All these conditions lead to a
		// termination of the recursion
		if (depth == 0 || hasWinner) {
			double score = 0;
			if (hasWinner) {
        // If maximizingPlayer is false, then the robot won
				score = maximizingPlayer ? LOSE_REVENUE : WIN_REVENUE;
			}
      else {
				score = UNCERTAIN_REVENUE;
			}

			return score / (MAX_DEPTH - depth + 1);
		}

		if (maximizingPlayer) {
			for (int col = 0; col < match.board.columns; col++) {
        try {
          match.makeMove(col);
          
          alpha =
					        Math.max(
					                alpha,
					                alphabeta(
                                  match,
					                        depth - 1,
					                        alpha,
					                        beta,
					                        false));
					
          match.undoMove();
					
          if (beta <= alpha) {
						break;
					}
        }
        catch (Exception ex) {
          // Ignore column
        }
			}
			return alpha;
		}
    else {
			for (int col = 0; col < match.board.columns; col++) {
        try {
          match.makeMove(col);
          
					beta =
					        Math.min(
					                beta,
					                alphabeta(
                                  match,
					                        depth - 1,
					                        alpha,
					                        beta,
					                        true));

          match.undoMove();
          
          if (beta <= alpha) {
						break;
					}
        }
        catch (Exception ex) {
          // Ignore column
        }
			}
			return beta;
		}
	}
}